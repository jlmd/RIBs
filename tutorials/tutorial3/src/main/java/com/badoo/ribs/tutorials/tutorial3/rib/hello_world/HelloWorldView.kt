package com.badoo.ribs.tutorials.tutorial3.rib.hello_world

import androidx.annotation.LayoutRes
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.tutorials.tutorial3.R
import com.badoo.ribs.tutorials.tutorial3.rib.hello_world.HelloWorldView.Event
import com.badoo.ribs.tutorials.tutorial3.rib.hello_world.HelloWorldView.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface HelloWorldView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event {
        object ButtonClicked : Event()
    }

    data class ViewModel(
        val i: Int = 0
    )

    interface Factory : ViewFactory<Nothing?, HelloWorldView>
}

class HelloWorldViewImpl private constructor(
    override val androidView: ViewGroup,
    private val events: PublishRelay<Event> = PublishRelay.create()
) : AndroidRibView(),
    HelloWorldView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_hello_world
    ) : HelloWorldView.Factory {
        override fun invoke(deps: Nothing?): (RibView) -> HelloWorldView = {
            HelloWorldViewImpl(
                it.inflate(layoutRes)
            )
        }
    }

    private val welcome: TextView = androidView.findViewById(R.id.hello_world_welcome)
    private val button: Button = androidView.findViewById(R.id.hello_world_button)

    init {
        button.setOnClickListener { events.accept(Event.ButtonClicked) }
    }

    override fun accept(vm: ViewModel) {
    }
}
