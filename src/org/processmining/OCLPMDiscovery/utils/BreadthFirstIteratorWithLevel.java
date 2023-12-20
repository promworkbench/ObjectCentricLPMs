package org.processmining.OCLPMDiscovery.utils;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.traverse.CrossComponentIterator;

/**
 * A breadth-first iterator for a directed or undirected graph which supports querying the depth of a vertex
 * in the search tree.
 * 
 * <p>
 * For this iterator to work correctly the graph must not be modified during iteration. Currently
 * there are no means to ensure that, nor to fail-fast. The results of such modifications are
 * undefined.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Joris Kinable
 * @since April 21, 2018
 */
public class BreadthFirstIteratorWithLevel<V, E>
    extends CrossComponentIterator<V, E, Integer>
{
    private Deque<V> queue = new ArrayDeque<>();

    /**
     * Creates a new breadth-first iterator for the specified graph.
     *
     * @param g the graph to be iterated.
     */
    public BreadthFirstIteratorWithLevel(Graph<V, E> g)
    {
        this(g, (V) null);
    }

    /**
     * Creates a new breadth-first iterator for the specified graph. Iteration will start at the
     * specified start vertex and will be limited to the connected component that includes that
     * vertex. If the specified start vertex is <code>null</code>, iteration will start at an
     * arbitrary vertex and will not be limited, that is, will be able to traverse all the graph.
     *
     * @param g the graph to be iterated.
     * @param startVertex the vertex iteration to be started.
     */
    public BreadthFirstIteratorWithLevel(Graph<V, E> g, V startVertex)
    {
        super(g, startVertex);
    }

    /**
     * Creates a new breadth-first iterator for the specified graph. Iteration will start at the
     * specified start vertices and will be limited to the connected component that includes those
     * vertices. If the specified start vertices is <code>null</code>, iteration will start at an
     * arbitrary vertex and will not be limited, that is, will be able to traverse all the graph.
     *
     * @param g the graph to be iterated.
     * @param startVertices the vertices iteration to be started.
     */
//    public BreadthFirstIteratorWithLevel(Graph<V, E> g, Iterable<V> startVertices)
//    {
//        super(g, startVertices);
//    }

    /**
     * @see CrossComponentIterator#isConnectedComponentExhausted()
     */
    @Override
    protected boolean isConnectedComponentExhausted()
    {
        return queue.isEmpty();
    }

    /**
     * @see CrossComponentIterator#encounterVertex(Object, Object)
     */
    @Override
    protected void encounterVertex(V vertex, E edge)
    {
        int depth= (edge == null ? 0 : getSeenData(Graphs.getOppositeVertex(getGraph(), edge, vertex))+1);
        putSeenData(vertex, depth);
        queue.add(vertex);
    }

    /**
     * @see CrossComponentIterator#encounterVertexAgain(Object, Object)
     */
    @Override
    protected void encounterVertexAgain(V vertex, E edge)
    {
    }

    /**
     * @see CrossComponentIterator#provideNextVertex()
     */
    @Override
    protected V provideNextVertex()
    {
        return queue.removeFirst();
    }

    public int getDepth(V v){
        assert getSeenData(v) != null;
        return getSeenData(v);
    }

}