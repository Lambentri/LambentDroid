package io.gmp.does.lambent.droid.ui.main

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import io.crossbar.autobahn.wamp.Client
import io.crossbar.autobahn.wamp.Session
import io.crossbar.autobahn.wamp.types.*
import io.crossbar.autobahn.websocket.exceptions.WebSocketException
import io.crossbar.autobahn.websocket.types.WebSocketOptions
import io.gmp.does.lambent.droid.log
import java.util.concurrent.CompletableFuture


open class MainViewModel(application: Application) : AndroidViewModel(application), Observable {
    // TODO: Implement the ViewModel
    val TAG: String = "io.gmp.does.droid.main"
    private val PREFS_NAME: String? = "LambentDroid"
    val shared_prefs =
        getApplication<Application>().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


    var connecting: Boolean = false;

    var connecting_viz: Int = if (connecting == true) View.VISIBLE else View.INVISIBLE
//    var connectionText : String = if (connecting == true) "Connectiong" else "Connect"
//    var connection_status = "pending"

    var session = Session()

    val host_name = ObservableField<String>()
    val host_port = ObservableField<String>()
    val host_realm = ObservableField<String>()

    val connection_status = ObservableField<String>()

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
    }

    fun link_listener(args: List<Any>, kwargs: Map<String, Any>, details: EventDetails) {
        log { "Got Event" }
    }

    fun add_subscriptions(session: Session, details: SessionDetails) {
        val subFuture: CompletableFuture<Subscription> = session.subscribe(
            "com.lambentri.edge.la4.links",
            ::link_listener
        )
        subFuture.whenComplete({ subscription, throwable ->
            if (throwable == null) {
                // We have successfully subscribed.
                System.out.println("Subscribed to topic " + subscription.topic)
            } else {
                // Something went bad.
                throwable.printStackTrace()
            }
        })
    }

    fun onJoin(session: Session, details: SessionDetails) {
        setPrefs()
        connection_status.set("Status: Connected to $host_name")
    }

    fun onDc(session: Session, details: CloseDetails) {
        connection_status.set("Status: Ready.")
    }


    fun onConnectButton() {
        log { "Connect Pressed" }
        connecting = true
        connection_status.set("connecting braj")
        notifyChange()
        start()
    }

    fun setPrefs() {
        val editor = shared_prefs.edit()
        editor.putString("host_name", host_name.get())
        editor.putString("host_port", host_port.get())
        editor.putString("host_realm", host_realm.get())
        editor.commit()
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
        log { hostname }


        val client = Client(session, hostname, host_realm.get())
        val exitInfoCompletableFuture: CompletableFuture<ExitInfo> = client.connect()


        connection_status.set("Status: Connecting to $wsuri ..")
//        setButtonDisconnect()
        val connectOptions = WebSocketOptions()
        connectOptions.reconnectInterval = 5000

    }

}