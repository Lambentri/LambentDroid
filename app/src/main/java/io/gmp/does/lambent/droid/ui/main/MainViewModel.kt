package io.gmp.does.lambent.droid.ui.main

import android.app.Application
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import androidx.databinding.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import io.crossbar.autobahn.wamp.Client
import io.crossbar.autobahn.wamp.Session
import io.crossbar.autobahn.wamp.types.*
import io.crossbar.autobahn.websocket.types.WebSocketOptions
import io.gmp.does.lambent.droid.*
import java.util.concurrent.CompletableFuture


val POSITION_TO_BRIGHTNESS = mapOf(
    0 to 0,
    1 to 3,
    2 to 7,
    3 to 15,
    4 to 31,
    5 to 63,
    6 to 127,
    7 to 255

)

val BRIGHTNESS_TO_POSITION = mapOf(
    255 to 7,
    127 to 6,
    63 to 5,
    31 to 4,
    15 to 3,
    7 to 2,
    3 to 1,
    0 to 0
)

val TEXT_CONNECT = "Connect"
val TEXT_DISCONNECT = "Disconnect"

data class BrightnessValue(
    var brightness: Int
)

open class MainViewModel(application: Application) : AndroidViewModel(application), Observable {
    val TAG: String = "io.gmp.does.droid.main"
    private val PREFS_NAME: String? = "LambentDroid"
    val shared_prefs =
        getApplication<Application>().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    //    lateinit var brightness_slider: Slider
    var connecting: Boolean = false;
    var joined: Boolean = false

    var connecting_viz = ObservableField<Int>()
    val connected_viz = ObservableField<Int>()
    var connectionText = ObservableField<String>()

    var session = Session()

    var curr_hostname = ""

    val host_name = ObservableField<String>()
    val host_port = ObservableField<String>()
    val host_realm = ObservableField<String>()

    val connection_status = ObservableField<String>()

    // storage of live data
    var brightnessSelectedPosition: Int = 0

    var list_links: MutableMap<String, Link> = ObservableArrayMap<String, Link>()
    var list_srcs: MutableList<LinkSrc> = ObservableArrayList<LinkSrc>()
    var list_sinks: MutableList<LinkSink> = ObservableArrayList<LinkSink>()
    var list_machines: MutableMap<String, Machine> = ObservableArrayMap<String, Machine>()
    var list_devices: MutableMap<String, Device> = ObservableArrayMap<String, Device>()

    //    var list_devices_l_t: MutableLiveData<List<Device>> = MutableLiveData()
    var list_devices_l_t = MutableLiveData<List<Device>>()
    var list_machined_l_t = MutableLiveData<List<Machine>>()
    var list_links_l_t = MutableLiveData<List<Link>>()


    companion object {
        @BindingAdapter("items_devices")
        @JvmStatic
        fun device_list(recyclerView: RecyclerView, items_devices: List<Device>) {
            val adapter = recyclerView.adapter as DeviceListAdapter
            adapter.setDevices(items_devices)
        }

        @BindingAdapter("items_machines")
        @JvmStatic
        fun machine_list(recyclerView: RecyclerView, args: List<Machine>) {
            val adapter = recyclerView.adapter as MachineListAdapter
            adapter.setMachines(args)
        }

        @BindingAdapter("items_links")
        @JvmStatic
        fun link_list(recyclerView: RecyclerView, args: List<Link>) {
            val adapter = recyclerView.adapter as LinkListAdapter
            adapter.setLinks(args)
        }
    }


    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }

    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    //
    init {
        host_name.set(shared_prefs.getString("host_name", "192.168.1.1"))
        host_port.set(shared_prefs.getString("host_port", "8083"));
        host_realm.set(shared_prefs.getString("host_realm", "realm1"));

        session.addOnJoinListener(::add_subscriptions)
        session.addOnJoinListener(::onJoin)
        session.addOnLeaveListener(::onDc)

        connecting_viz.set(View.VISIBLE)
        connected_viz.set(View.GONE)
        connectionText.set(TEXT_CONNECT)


        list_devices_l_t.value = emptyList<Device>()
        list_machined_l_t.value = emptyList<Machine>()
        list_links_l_t.value = emptyList<Link>()
    }

    fun brightness_listener(args: List<Any>, kwargs: Map<String, Any>, details: EventDetails) {
        log { "Got Brightness Update" }
        log { kwargs.toString() }
        log { details.toString() }
        val brightness = kwargs.get("brightness") as Int
        brightnessSelectedPosition = BRIGHTNESS_TO_POSITION[brightness]!!
    }

    fun link_listener(args: List<Any>, kwargs: Map<String, Any>, details: EventDetails) {
//        log { "Got Link Update" }
//        log { kwargs.toString() }
//        log { details.toString() }

        val links = kwargs.get("links") as Map<String, Map<String, Any>>
        val sinks = kwargs.get("sinks") as List<Map<String, String>>
        val srcs = kwargs.get("srcs") as List<Map<String, String>>
//        for ((key: String, entry: Map<String, Any>) in links) {
//            list_links[key] = Link.fromNetwork(entry)
//        }
        list_links = links.map { it.key to Link.fromNetwork(it.value) }.toMap().toMutableMap()
        list_sinks = sinks.map { LinkSink.fromNetwork(it) }.toMutableList()
        list_srcs = srcs.map { LinkSrc.fromNetwork(it) }.toMutableList()
//        for (entry in sinks) {
//            val s = LinkSink.fromNetwork(entry)
//            list_sinks[s.id] = s
//        }
//
//        for (entry in srcs) {
//            val s = LinkSrc.fromNetwork(entry)
//            list_srcs[s.id] = s
//        }
        list_links_l_t.value = list_links.values.toList().sortedBy { it.list_name }
        notifyChange()

    }

    fun device_listener(args: List<Any>, kwargs: Map<String, Any>, details: EventDetails) {
//        log { "Got Device Update" }
//        log { kwargs.toString() }
//        log { details.toString() }

        val entries: ArrayList<Map<String, String>> =
            kwargs.get("res") as ArrayList<Map<String, String>>

        for (entry in entries) {
            val d = Device.fromNetwork(entry)
            list_devices[d.id] = d
        }
        list_devices_l_t.value = list_devices.values.toList().sortedBy { it.name }


        log { list_devices.toString() }
    }

    fun machine_collector(session: Session) {
        log { "Collecting Machines" }
        if (session.isConnected()) {
            session.call("com.lambentri.edge.la4.machine.list").thenAccept { it ->
                log { "Collected" }
                val machine_result =
                    MachineQuery.fromNetwork(it.results.get(0) as Map<String, String>)
                for (entry: Machine in machine_result.machines.values) {
                    list_machines[entry.id] = entry
                }
                list_machined_l_t.value = list_machines.values.toList().sortedBy { it.name }
                notifyChange()
            }
        }
    }


    fun add_subscriptions(session: Session, details: SessionDetails) {
        val brightnessControlFuture: CompletableFuture<Subscription> = session.subscribe(
            "com.lambentri.edge.la4.machine.gb",
            ::brightness_listener
        )

        brightnessControlFuture.whenComplete { subscription, throwable ->
            if (throwable == null) {
                // We have successfully subscribed.
                log { "Subscribed to topic " + subscription.topic }
                System.out.println("Subscribed to topic " + subscription.topic)
            } else {
                // Something went bad.
                throwable.printStackTrace()
            }
        }

        val subLinksFuture: CompletableFuture<Subscription> = session.subscribe(
            "com.lambentri.edge.la4.links",
            ::link_listener
        )
        subLinksFuture.whenComplete { subscription, throwable ->
            if (throwable == null) {
                // We have successfully subscribed.
                log { "Subscribed to topic " + subscription.topic }
                System.out.println("Subscribed to topic " + subscription.topic)
            } else {
                // Something went bad.
                throwable.printStackTrace()
            }
        }

        val subDevicesFuture: CompletableFuture<Subscription> = session.subscribe(
            "com.lambentri.edge.la4.machine.sink.8266-7777", // TODO generic device governor
            ::device_listener
        )
        subDevicesFuture.whenComplete { subscription, throwable ->
            if (throwable == null) {
                // We have successfully subscribed.
                log { "Subscribed to topic " + subscription.topic }
                System.out.println("Subscribed to topic " + subscription.topic)
            } else {
                // Something went bad.
                throwable.printStackTrace()
            }
        }

    }

    fun onJoin(session: Session, details: SessionDetails) {
        setPrefs()
        connection_status.set("Status: Connected to $curr_hostname")
        log { "joined" }
        connecting_viz.set(View.GONE)
        connected_viz.set(View.VISIBLE)
        connectionText.set(TEXT_DISCONNECT)


        session.call("com.lambentri.edge.la4.machine.gb.get").thenAccept {
            log { it.results.get(0).toString() }
            val pos = (it.results.get(0) as Map<String, Int>).get("brightness")
            log { pos.toString() }
            brightnessSelectedPosition = BRIGHTNESS_TO_POSITION[pos!!]!!
            notifyChange()
        }

        joined = true
    }

    fun onDc(session: Session, details: CloseDetails) {
        joined = false
        connection_status.set("Status: Ready to Connect")
        log { "dcd" }
        connecting_viz.set(View.VISIBLE)
        connected_viz.set(View.GONE)
        connectionText.set(TEXT_CONNECT)
    }


    fun onConnectButton() {
        if (!connecting) {
            log { "Connect Pressed" }
            connecting = true
            connection_status.set("connecting braj")
            notifyChange()
            start()
        } else {
            log { "Disconnect pressed" }
            connecting = false
            session.leave()
            notifyChange()
//            connection_button
        }
    }

    fun setPrefs() {
        val editor = shared_prefs.edit()
        editor.putString("host_name", host_name.get())
        editor.putString("host_port", host_port.get())
        editor.putString("host_realm", host_realm.get())
        editor.commit()
    }

    val selectedSpeed = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val currentItem = parent!!.getItemAtPosition(position) as String
            if (currentItem != null && joined) {
                log { currentItem.toString() }
                brightnessSelectedPosition = position
                if (session.isConnected()) {
                    session.call(
                        "com.lambentri.edge.la4.machine.gb.set",
                        POSITION_TO_BRIGHTNESS[position]
                    )
                }
            }
        }
    }

    fun off() {
        if (session.isConnected()) {
            session.call(
                "com.lambentri.edge.la4.machine.gb.set",
                POSITION_TO_BRIGHTNESS[0]
            )
        }
    }

    fun device_poke(entryId: String) {
        log { "netPOKER " }
        if (session.isConnected()) {
            session.call(
                "com.lambentri.edge.la4.device.82667777.poke",
                entryId
            )
        }
    }

    fun device_poke_subtle(entryId: String) {
        log { "netPOKER S" }
        if (session.isConnected()) {
            session.call(
                "com.lambentri.edge.la4.device.82667777.pokes",
                entryId
            )
        }
    }

    fun device_rename(entryIname: String, newName: String) {
        log { "netRENAMER" }
        if (session.isConnected()) {
            val call_map = mapOf<String, String>(
                "shortname" to entryIname,
                "nicename" to newName
            )
            session.call(
                "com.lambentri.edge.la4.device.82667777.name",
                call_map
            )
        }
    }

    fun device_rescan() {
        log { "devicePunisher" }
        if (session.isConnected()) {
            session.call(
                "com.lambentri.edge.la4.device.82667777.rescan"
            )
        }
    }

    fun machine_faster(machine: Machine) {
        log { "netMachFaster" }
        if (session.isConnected()) {
            session.call(
                "com.lambentri.edge.la4.machine.tick_up",
                machine.id
            ).thenAccept {
                val res = it.results.get(0) as Map<String, String>
                log { res.toString() }
                this.machine_collector(this.session)
                this.machine_collector(this.session)
            }
        }
    }

    fun machine_slower(machine: Machine) {
        log { "netMachSlower" }
        if (session.isConnected()) {
            session.call(
                "com.lambentri.edge.la4.machine.tick_dn",
                machine.id
            ).thenAccept {
                val res = it.results.get(0) as Map<String, String>
                log { res.toString() }
                this.machine_collector(this.session)
                this.machine_collector(this.session)
            }
        }
    }

    fun machine_playpause(machine: Machine) {
        log { "netMachSlower" }
        if (session.isConnected()) {
            session.call(
                "com.lambentri.edge.la4.machine.pause",
                machine.id
            ).thenAccept {
                log { it.results.get(0).toString() }
                this.machine_collector(this.session)
                this.machine_collector(this.session)
            }
        }
    }

    fun machine_mk() {

    }

    fun machine_rm() {

    }

    fun link_mk(src : LinkSrc, tgt: LinkSink, name: String) {
        val payload = mapOf(
            "link_name" to name,
            "link_spec" to mapOf(
                "source" to src.toNetwork(),
                "target" to tgt.toNetwork()
            )
        )

        if (session.isConnected()) {
            session.call(
                "com.lambentri.edge.la4.links.save",
                payload
            ).thenAccept {
                log { "mkmkmkmk"}
                log { it.results.get(0).toString() }
            }
        }
    }


    fun link_mk_bulk(src : LinkSrc, tgt: List<LinkSink>, name: String) {
        val payload = mapOf(
            "link_name" to name,
            "link_spec" to mapOf(
                "source" to src.toNetwork(),
                "targets" to tgt.map{it.toNetwork()}
            )
        )
        if (session.isConnected()) {
            session.call(
                "com.lambentri.edge.la4.links.save_bulk",
                payload
            ).thenAccept {
                log { "mkmkmkmk"}
                log { it.results.get(0).toString() }
            }
        }
    }

    fun link_rm() {

    }

    fun link_rm_bulk() {
        log { "bulkClear" }
        if (session.isConnected()) {
            session.call(
                "com.lambentri.edge.la4.links.destroy_all"
            ).thenAccept {
                log { it.results.get(0).toString() }
            }
        }
    }

    fun link_toggle(link: Link) {
        log { "netLinkToggle" }
        if (session.isConnected()) {
            session.call(
                "com.lambentri.edge.la4.links.toggle",
                link.name
            ).thenAccept {
                log { it.results.get(0).toString() }
            }
        }
    }

    fun query_and_set_names(editText: EditText) {
        var result = "a"
        if (session.isConnected()) {
            session.call(
                "com.lambentri.edge.la4.helpers.namegen"
            ).thenAccept {
                log { it.results.get(0).toString() }
                val netresult = it.results.get(0) as Map<String, String>
                result = netresult.getOrDefault("name", "")
                editText.setText(result)
            }
        }
    }


    private fun start() {
        var hostname: String = host_name.get()!!
        if (!hostname.startsWith("ws://") && !hostname.startsWith("wss://")) {
            hostname = "ws://$hostname"
        }
        val port: String = host_port.get()!!
        val wsuri: String
        wsuri = if (!port.isEmpty()) {
            "$hostname:$port"
        } else {
            hostname
        }

        hostname = "$hostname:$port/ws"
        curr_hostname = hostname
        log { hostname }
        log { port }
        log { host_realm.get().toString() }

        val client = Client(session, hostname, host_realm.get())
        val exitInfoCompletableFuture: CompletableFuture<ExitInfo> = client.connect()


        connection_status.set("Status: Connecting to $wsuri ..")
//        setButtonDisconnect()
        val connectOptions = WebSocketOptions()
        connectOptions.reconnectInterval = 5000

    }
}