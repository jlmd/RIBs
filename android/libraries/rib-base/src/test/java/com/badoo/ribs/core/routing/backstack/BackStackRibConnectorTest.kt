//package com.badoo.ribs.core.routing.backstack
//
//import android.os.Bundle
//import android.os.Parcelable
//import com.badoo.ribs.core.Node
//import com.badoo.ribs.core.routing.action.RoutingAction
//import com.nhaarman.mockitokotlin2.any
//import com.nhaarman.mockitokotlin2.argumentCaptor
//import com.nhaarman.mockitokotlin2.doAnswer
//import com.nhaarman.mockitokotlin2.doReturn
//import com.nhaarman.mockitokotlin2.inOrder
//import com.nhaarman.mockitokotlin2.mock
//import com.nhaarman.mockitokotlin2.never
//import com.nhaarman.mockitokotlin2.verify
//import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
//import kotlinx.android.parcel.Parcelize
//import org.junit.Assert.assertEquals
//import org.junit.Assert.assertNotNull
//import org.junit.Assert.assertNull
//import org.junit.Before
//import org.junit.Test
//
//class BackStackRibConnectorTest {
//
//    sealed class Configuration : Parcelable {
//        @Parcelize object C1 : Configuration()
//        @Parcelize object C2 : Configuration()
//    }
//
//    private lateinit var backStackRibConnector: ChildNodeConnector<Configuration>
//    private lateinit var backStackRibManager: BackStackManager<Configuration>
//    private lateinit var resolver: (Configuration) -> RoutingAction<*>
//    private lateinit var parentNode: Node<*>
//
//    private lateinit var routingAction1: RoutingAction<*>
//    private lateinit var routingAction2: RoutingAction<*>
//    private lateinit var node1_1: Node<*>
//    private lateinit var node1_2: Node<*>
//    private lateinit var node1_3: Node<*>
//    private lateinit var node2_1: Node<*>
//    private lateinit var node2_2: Node<*>
//    private lateinit var ribs1: List<Node.Descriptor>
//    private lateinit var ribs2: List<Node.Descriptor>
//    private lateinit var ribsFactories1: List<() -> Node.Descriptor>
//    private lateinit var ribsFactories2: List<() -> Node.Descriptor>
//    private lateinit var backStackElement1: ConnectorConfiguration<Configuration>
//    private lateinit var backStackElement2: ConnectorConfiguration<Configuration>
//
//    @Before
//    fun setUp() {
//        node1_1 = mock()
//        node1_2 = mock()
//        node1_3 = mock()
//        node2_1 = mock()
//        node2_2 = mock()
//        ribs1 = listOf(node1_1, node1_2, node1_3).map { Node.Descriptor(it, Node.ViewAttachMode.PARENT) }
//        ribs2 = listOf(node2_1, node2_2).map { Node.Descriptor(it, Node.ViewAttachMode.PARENT) }
//        ribsFactories1 = ribs1.map { nodeDescriptor ->
//            mock<() -> Node.Descriptor> { on { invoke() } doReturn nodeDescriptor }
//        }
//        ribsFactories2 = ribs2.map { nodeDescriptor ->
//            mock<() -> Node.Descriptor> { on { invoke() } doReturn nodeDescriptor }
//        }
//
//        backStackElement1 = ConnectorConfiguration(configuration = Configuration.C1)
//        backStackElement2 = ConnectorConfiguration(configuration = Configuration.C2)
//
//        routingAction1 = mock { on { buildNodes() } doAnswer { ribsFactories1.map { it.invoke() } }}
//        routingAction2 = mock { on { buildNodes() } doAnswer { ribsFactories2.map { it.invoke() } }}
//
//        resolver = mock {
//            on { invoke(Configuration.C1) } doReturn routingAction1
//            on { invoke(Configuration.C2) } doReturn routingAction2
//        }
//
//        // FIXME test reacting to news of manager:
//        backStackRibManager = mock()
//        parentNode = mock()
//        val permanentParts = emptyList<Node<*>>() // FIXME test this too
//        backStackRibConnector = ChildNodeConnector(backStackRibManager, permanentParts, resolver, parentNode)
//    }
//
//    @Test
//    fun `When leaving BackStackElement with DESTROY, cleanup() is called on associated RoutingAction`() {
//        backStackElement1.routingAction = routingAction1
//        backStackRibConnector.leave(backStackElement1, DESTROY)
//        verify(routingAction1).cleanup()
//    }
//
//    @Test
//    fun `When leaving BackStackElement with DESTROY, all of its nodes are detached`() {
//        backStackElement1.builtNodes = ribs1
//        backStackRibConnector.leave(backStackElement1, DESTROY)
//        ribs1.forEach {
//            inOrder(parentNode) {
//                verify(parentNode).detachChildView(it.node)
//                verify(parentNode).detachChildNode(it.node)
//            }
//        }
//        verifyNoMoreInteractions(parentNode)
//    }
//
//    @Test
//    fun `When leaving BackStackElement with DESTROY, all RIB references are cleared`() {
//        backStackElement1.builtNodes = ribs1
//        backStackRibConnector.leave(backStackElement1, DESTROY)
//        assertEquals(null, backStackElement1.builtNodes)
//    }
//
//    @Test
//    fun `When leaving BackStackElement with DETACH_VIEW, cleanup() is called on associated RoutingAction`() {
//        backStackElement1.routingAction = routingAction1
//        backStackRibConnector.leave(backStackElement1, DETACH_VIEW)
//        verify(routingAction1).cleanup()
//    }
//
//    @Test
//    fun `When leaving BackStackElement with DETACH_VIEW, saveViewState() is called on all children`() {
//        backStackElement1.builtNodes = ribs1
//        backStackRibConnector.leave(backStackElement1, DETACH_VIEW)
//        ribs1.forEach {
//            verify(it.node).saveViewState()
//        }
//    }
//
//    @Test
//    fun `When leaving BackStackElement with DETACH_VIEW, all children are detached from view`() {
//        backStackElement1.builtNodes = ribs1
//        backStackRibConnector.leave(backStackElement1, DETACH_VIEW)
//        ribs1.forEach {
//            verify(parentNode).detachChildView(it.node)
//        }
//        verifyNoMoreInteractions(parentNode)
//    }
//
//    @Test
//    fun `When leaving BackStackElement with DETACH_VIEW, RIB references are kept`() {
//        backStackElement1.builtNodes = ribs1
//        backStackRibConnector.leave(backStackElement1, DETACH_VIEW)
//        assertEquals(ribs1, backStackElement1.builtNodes)
//    }
//
//    @Test
//    fun `When going to BackStackElement, routing action is resolved on demand`() {
//        backStackRibConnector.goTo(backStackElement1)
//        verify(resolver).invoke(backStackElement1.configuration)
//    }
//
//    @Test
//    fun `When going to BackStackElement, execute() is called on associated RoutingAction`() {
//        backStackRibConnector.goTo(backStackElement1)
//        verify(routingAction1).execute()
//    }
//
//    @Test
//    fun `When going to BackStackElement, RIB factories of the associated RoutingAction are invoked`() {
//        backStackRibConnector.goTo(backStackElement1)
//        ribsFactories1.forEach {
//            verify(it).invoke()
//        }
//    }
//
//    @Test
//    fun `When going to BackStackElement, RIBs that are created are attached`() {
//        backStackRibConnector.goTo(backStackElement1)
//        ribs1.forEach {
//            verify(parentNode).attachChildNode(it.node)
//        }
//    }
//
//    @Test
//    fun `When going to BackStackElement, if it already has some RIBs alive then they are attached to the view`() {
//        backStackElement1.builtNodes = ribs1
//        backStackRibConnector.goTo(backStackElement1)
//        ribs1.forEach {
//            verify(parentNode).attachChildView(it.node)
//        }
//        verifyNoMoreInteractions(parentNode)
//    }
//
//    @Test
//    fun `saveInstanceState() returns modified back stack`() {
//        backStackElement1.builtNodes = ribs1
//        backStackElement2.builtNodes = ribs2
//        val backStack = listOf(backStackElement1, backStackElement2)
//        val returnedBackStack = backStackRibConnector.saveInstanceState(backStack)
//        assertEquals(backStack, returnedBackStack)
//    }
//
//    @Test
//    fun `saveInstanceState() returns back stack that contains bundles`() {
//        backStackElement1.builtNodes = ribs1
//        backStackElement2.builtNodes = ribs2
//        val backStack = listOf(backStackElement1, backStackElement2)
//        val returnedBackStack = backStackRibConnector.saveInstanceState(backStack)
//
//        returnedBackStack.forEach {
//            assertNotNull(it.bundles)
//        }
//    }
//
//    @Test
//    fun `saveInstanceState() calls saveInstanceState() on all RIBs in back stack`() {
//        backStackElement1.builtNodes = ribs1
//        backStackElement2.builtNodes = ribs2
//        val backStack = listOf(backStackElement1, backStackElement2)
//        backStackRibConnector.saveInstanceState(backStack)
//
//        listOf(ribs1, ribs2).forEach {
//            it.forEach {
//                verify(it.node).onSaveInstanceState(any())
//            }
//        }
//    }
//
//    @Test
//    fun `saveInstanceState() saves bundles of RIBs in the back stack`() {
//        backStackElement1.builtNodes = ribs1
//        backStackElement2.builtNodes = ribs2
//        val backStack = listOf(backStackElement1, backStackElement2)
//        backStackRibConnector.saveInstanceState(backStack)
//        val expectedBundles1 = mutableListOf<Bundle>()
//        val expectedBundles2 = mutableListOf<Bundle>()
//
//        listOf(
//            ribs1 to expectedBundles1,
//            ribs2 to expectedBundles2
//        ).forEach {
//            val (ribList, bundleList) = it
//            ribList.forEach {
//                val captor = argumentCaptor<Bundle>()
//                verify(it.node).onSaveInstanceState(captor.capture())
//                bundleList.add(captor.firstValue)
//            }
//        }
//
//        assertEquals(expectedBundles1, backStackElement1.bundles)
//        assertEquals(expectedBundles2, backStackElement2.bundles)
//    }
//
//    @Test
//    fun `saveInstanceState() does not do any cleanup`() {
//        backStackElement1.apply {
//            builtNodes = ribs1
//            routingAction = routingAction1
//        }
//        backStackElement2.apply {
//            builtNodes = ribs2
//            routingAction = routingAction2
//        }
//        val backStack = listOf(backStackElement1, backStackElement2)
//        backStackRibConnector.saveInstanceState(backStack)
//        verify(routingAction1, never()).cleanup()
//        verify(routingAction2, never()).cleanup()
//    }
//
//    @Test
//    fun `saveInstanceState() does not clear rib references`() {
//        backStackElement1.builtNodes = ribs1
//        backStackElement2.builtNodes = ribs2
//        val backStack = listOf(backStackElement1, backStackElement2)
//        val returnedBackStack = backStackRibConnector.saveInstanceState(backStack)
//
//        returnedBackStack.forEach {
//            assertNotNull(it.builtNodes)
//        }
//    }
//
//    @Test
//    fun `shrinkToBundles() returns modified back stack`() {
//        backStackElement1.builtNodes = ribs1
//        backStackElement2.builtNodes = ribs2
//        val backStack = listOf(backStackElement1, backStackElement2)
//        val returnedBackStack = backStackRibConnector.shrinkToBundles(backStack)
//        assertEquals(backStack, returnedBackStack)
//    }
//
//    @Test
//    fun `shrinkToBundles() returns back stack that contains bundles`() {
//        backStackElement1.builtNodes = ribs1
//        backStackElement2.builtNodes = ribs2
//        val backStack = listOf(backStackElement1, backStackElement2)
//        val returnedBackStack = backStackRibConnector.shrinkToBundles(backStack)
//
//        returnedBackStack.forEach {
//            assertNotNull(it.bundles)
//        }
//    }
//
//    @Test
//    fun `shrinkToBundles() calls saveInstanceState() on all RIBs in back stack`() {
//        backStackElement1.builtNodes = ribs1
//        backStackElement2.builtNodes = ribs2
//        val backStack = listOf(backStackElement1, backStackElement2)
//        backStackRibConnector.shrinkToBundles(backStack)
//
//        listOf(ribs1, ribs2).forEach {
//            it.forEach {
//                verify(it.node).onSaveInstanceState(any())
//            }
//        }
//    }
//
//    @Test
//    fun `shrinkToBundles() saves bundles of RIBs in the back stack`() {
//        backStackElement1.builtNodes = ribs1
//        backStackElement2.builtNodes = ribs2
//        val backStack = listOf(backStackElement1, backStackElement2)
//        backStackRibConnector.shrinkToBundles(backStack)
//        val expectedBundles1 = mutableListOf<Bundle>()
//        val expectedBundles2 = mutableListOf<Bundle>()
//
//        listOf(
//            ribs1 to expectedBundles1,
//            ribs2 to expectedBundles2
//        ).forEach {
//            val (ribList, bundleList) = it
//            ribList.forEach {
//                val captor = argumentCaptor<Bundle>()
//                verify(it.node).onSaveInstanceState(captor.capture())
//                bundleList.add(captor.firstValue)
//            }
//        }
//
//        assertEquals(expectedBundles1, backStackElement1.bundles)
//        assertEquals(expectedBundles2, backStackElement2.bundles)
//    }
//
//    @Test
//    fun `shrinkToBundles() returns clears RIB references in all but the last back stack element`() {
//        backStackElement1.builtNodes = ribs1
//        backStackElement2.builtNodes = ribs2
//        val backStack = listOf(backStackElement1, backStackElement2)
//        val returnedBackStack = backStackRibConnector.shrinkToBundles(backStack)
//
//        assertNull(returnedBackStack[0].builtNodes)
//        assertNotNull(returnedBackStack[1].builtNodes)
//    }
//
//    @Test
//    fun `shrinkToBundles() does not call cleanup() on last routing action`() {
//        backStackElement1.apply {
//            builtNodes = ribs1
//            routingAction = routingAction1
//        }
//        backStackElement2.apply {
//            builtNodes = ribs2
//            routingAction = routingAction2
//        }
//        val backStack = listOf(backStackElement1, backStackElement2)
//        backStackRibConnector.shrinkToBundles(backStack)
//        verify(routingAction2, never()).cleanup()
//        verifyNoMoreInteractions(routingAction1)
//        verifyNoMoreInteractions(routingAction2)
//    }
//
//    @Test
//    fun `shrinkToBundles() detaches RIBs in all but the last back stack element`() {
//        backStackElement1.builtNodes = ribs1
//        backStackElement2.builtNodes = ribs2
//        val backStack = listOf(backStackElement1, backStackElement2)
//        backStackRibConnector.shrinkToBundles(backStack)
//
//        ribs1.forEach {
//            inOrder(parentNode) {
//                verify(parentNode).detachChildView(it.node)
//                verify(parentNode).detachChildNode(it.node)
//            }
//        }
//
//        ribs2.forEach {
//            verify(parentNode, never()).detachChildView(it.node)
//            verify(parentNode, never()).detachChildNode(it.node)
//        }
//    }
//
//    @Test
//    fun `tearDown() detaches all RIBs in back stack`() {
//        backStackElement1.routingAction = routingAction1
//        backStackElement2.routingAction = routingAction2
//        val backStack = listOf(backStackElement1, backStackElement2)
//        backStackRibConnector.detachFromView()
//
//        verify(routingAction2).cleanup()
//    }
//}
