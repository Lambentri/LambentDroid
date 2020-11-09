package io.gmp.does.lambent.droid.ui.main

import android.app.Application
import android.content.Context
import android.view.View
import android.widget.AdapterView
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import io.crossbar.autobahn.wamp.Client
import io.crossbar.autobahn.wamp.Session
import io.crossbar.autobahn.wamp.types.*
import io.crossbar.autobahn.websocket.types.WebSocketOptions
import io.gmp.does.lambent.droid.log
import java.util.concurrent.CompletableFuture

val POSITION_TO_BRIGHTNESS = mapOf(
    0 to 0,
    1 to 20,
    2 to 10,
    3 to 4,
    4 to 2,
    5 to 1
)

val BRIGHTNESS_TO_POSITION = mapOf(
    0 to 0,
    20 to 1,
    10 to 2,
    4 to 3,
    2 to 4,
    1 to 5
)

val TEXT_CONNECT = "Connect"
val TEXT_DISCONNECT = "Disconnect"

data class BrightnessValue(
    var brightness: Int
)
open class MainViewModel(application: Application) : AndroidViewModel(application), Observable {
    // TODO: Implement the ViewModel
    val TAG: String = "io.gmp.does.droid.main"
    private val PREFS_NAME: String? = "LambentDroid"
    val shared_prefs =
        getApplication<Application>().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

//    lateinit var brightness_slider: Slider
    var connecting: Boolean = false;

    var connecting_viz = ObservableField<Int>()
    var connectionText = ObservableField<String>()

    var session = Session()

    var curr_hostname = ""

    val host_name = ObservableField<String>()
    val host_port = ObservableField<String>()
    val host_realm = ObservableField<String>()

    val connection_status = ObservableField<String>()

    var brightnessSelectedPosition: Int = 0

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
        notifyChange()

        session.addOnJoinListener(::add_subscriptions)
        session.addOnJoinListener(::onJoin)
        session.addOnLeaveListener(::onDc)

        connecting_viz.set(View.VISIBLE)
        connectionText.set(TEXT_CONNECT)


    }

    fun link_listener(args: List<Any>, kwargs: Map<String, Any>, details: EventDetails) {
//        log { "Got Event" }
//        log { kwargs.toString() }
//        log { details.toString() }
    }

    fun device_listener(args: List<Any>, kwargs: Map<String, Any>, details: EventDetails) {

    }

    fun add_subscriptions(session: Session, details: SessionDetails) {
        val subLinksFuture: CompletableFuture<Subscription> = session.subscribe(
            "com.lambentri.edge.la4.links",
            ::link_listener
        )
        subLinksFuture.whenComplete({ subscription, throwable ->
            if (throwable == null) {
                // We have successfully subscribed.
                log { "Subscribed to topic " + subscription.topic }
                System.out.println("Subscribed to topic " + subscription.topic)
            } else {
                // Something went bad.
                throwable.printStackTrace()
            }
        })

        val subDevicesFuture: CompletableFuture<Subscription> = session.subscribe(
            "com.lambentri.edge.la4.links",
            ::device_listener
        )
        subDevicesFuture.whenComplete({ subscription, throwable ->
            if (throwable == null) {
                // We have successfully subscribed.
                log { "Subscribed to topic " + subscription.topic }
                System.out.println("Subscribed to topic " + subscription.topic)
            } else {
                // Something went bad.
                throwable.printStackTrace()
            }
        })
    }

    fun onJoin(session: Session, details: SessionDetails) {
        setPrefs()
        connection_status.set("Status: Connected to $curr_hostname")
        log { "joined" }
        connecting_viz.set(View.GONE)
        connectionText.set(TEXT_DISCONNECT)

        session.call("com.lambentri.edge.la4.machine.gb.get").thenAccept {
            log{it.results.get(0).toString()}
            val pos = (it.results.get(0) as Map<String,Int>).get("brightness")
            log { pos.toString() }
            brightnessSelectedPosition = BRIGHTNESS_TO_POSITION[pos!!]!!
            notifyChange()
        }
    }

    fun onDc(session: Session, details: CloseDetails) {
        connection_status.set("Status: Ready to Connect")
        log { "dcd" }
        connecting_viz.set(View.VISIBLE)
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
            log {"Disconnect pressed"}
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
            if (currentItem != null) {
                log { currentItem.toString() }
                if(session.isConnected()) {
                    session.call("com.lambentri.edge.la4.machine.gb.set", POSITION_TO_BRIGHTNESS[position])
                }
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