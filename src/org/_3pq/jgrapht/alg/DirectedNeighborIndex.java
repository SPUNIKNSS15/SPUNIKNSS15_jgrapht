/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Lead:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2004, by Barak Naveh and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
/* --------------------------
 * DirectedNeighborIndex.java
 * --------------------------
 * (C) Copyright 2003, by Barak Naveh and Contributors.
 *
 * Original Author:  Charles Fry
 *
 * $Id$
 *
 * Changes
 * -------
 * 13-Dec-2005 : Initial revision (CF);
 *
 */
package org._3pq.jgrapht.alg;

import java.util.*;

import org._3pq.jgrapht.*;
import org._3pq.jgrapht.alg.NeighborIndex.Neighbors;
import org._3pq.jgrapht.event.*;
import org._3pq.jgrapht.graph.*;

/**
 * Maintains a cache of each vertex's neighbors. While
 * lists of neighbors can be obtained from {@link GraphHelper}, they are
 * re-calculated at each invocation by walking a vertex's incident edges. Which
 * becomes inordinately expensive when performed often.
 *
 * <p>A verteces' neighbors are cached the first time they are asked for (i.e.
 * the index is built on demand). The index will only be updated automatically
 * if it is added to the associated grpah as a listener. If it is added as a
 * listener to a graph other than the one it indexes, results are
 * undefined.</p>
 *
 * @author Charles Fry
 * @since Dec 13, 2005
 */
public class DirectedNeighborIndex<V, E extends Edge<V>> implements GraphListener<V, E>
{
    //~ Instance fields -------------------------------------------------------

    Map<V, Neighbors> m_predecessorMap = new HashMap<V, Neighbors>();
    Map<V, Neighbors> m_successorMap = new HashMap<V, Neighbors>();
    private DirectedGraph<V, E> m_graph;


    //~ Constructors ----------------------------------------------------------

    /**
     * Creates a neighbor index for the specified directed graph.
     *
     * @param g the graph for which a neighbor index is to be created.
     */
    public DirectedNeighborIndex(DirectedGraph<V, E> g)
    {
        m_graph = g;
    }

    //~ Methods ---------------------------------------------------------------

    /**
     * Returns the set of vertices which are the predecessors of a specified
     * vertex. The returned set is backed
     * by the index, and will be updated when the graph changes as long as
     * the index has been added as a listener to the graph.
     *
     * @param v the vertex whose predecessors are desired
     * @return all unique predecessors of the specified vertex
     */
    public Set<V> predecessorsOf(V v)
    {
        return getPredecessors(v).getNeighbors();
    }

    /**
     * Returns the set of vertices which are the predecessors of a specified
     * vertex. If the graph is a multigraph, vertices may appear more than
     * once in the returned list. Because a list of
     * predecessors can not be efficiently maintained, it is reconstructed
     * on every invocation by duplicating entries in the neighbor set.
     * It is thus more effecient to use {@link neighborsOf(V)}
     * unless dupliate neighbors are required.
     *
     * @param v the vertex whose predecessors are desired
     * @return all predecessors of the specified vertex
     */
    public List<V> predecessorListOf(V v)
    {
        return getPredecessors(v).getNeighborList();
    }

    /**
     * Returns the set of vertices which are the successors of a specified
     * vertex. The returned set is backed
     * by the index, and will be updated when the graph changes as long as
     * the index has been added as a listener to the graph.
     *
     * @param v the vertex whose successors are desired
     * @return all unique successors of the specified vertex
     */
    public Set<V> successorsOf(V v)
    {
        return getSuccessors(v).getNeighbors();
    }

    /**
     * Returns the set of vertices which are the successors of a specified
     * vertex. If the graph is a multigraph, vertices may appear more than
     * once in the returned list. Because a list of
     * successors can not be efficiently maintained, it is reconstructed
     * on every invocation by duplicating entries in the neighbor set.
     * It is thus more effecient to use {@link neighborsOf(V)}
     * unless dupliate neighbors are required.
     *
     * @param v the vertex whose successors are desired
     * @return all successors of the specified vertex
     */
    public List<V> successorListOf(V v)
    {
        return getSuccessors(v).getNeighborList();
    }

    /**
     * @see GraphListener#edgeAdded(GraphEdgeChangeEvent)
     */
    public void edgeAdded(GraphEdgeChangeEvent<V, E> e)
    {
        E edge = e.getEdge();
        V source = edge.getSource();
        V target = edge.getTarget();
        getSuccessors(source).addNeighbor(target);
        getPredecessors(target).addNeighbor(source);
    }

    /**
     * @see GraphListener#edgeRemoved(GraphEdgeChangeEvent)
     */
    public void edgeRemoved(GraphEdgeChangeEvent<V, E> e)
    {
        E edge = e.getEdge();
        V source = edge.getSource();
        V target = edge.getTarget();
        if (m_successorMap.containsKey(source)) {
            m_successorMap.get(source).removeNeighbor(target);
        }
        if (m_predecessorMap.containsKey(target)) {
            m_predecessorMap.get(target).removeNeighbor(source);
        }
    }

    /**
     * @see org._3pq.jgrapht.event.VertexSetListener#vertexAdded(GraphVertexChangeEvent)
     */
    public void vertexAdded(GraphVertexChangeEvent<V> e)
    {
        // nothing to cache until there are edges
    }

    /**
     * @see org._3pq.jgrapht.event.VertexSetListener#vertexRemoved(GraphVertexChangeEvent)
     */
    public void vertexRemoved(GraphVertexChangeEvent<V> e)
    {
        m_predecessorMap.remove(e.getVertex());
        m_successorMap.remove(e.getVertex());
    }

    private Neighbors getPredecessors(V v)
    {
        Neighbors neighbors = m_predecessorMap.get(v);
        if (neighbors == null) {
            neighbors = new Neighbors<V, E>(v,
                    GraphHelper.predecessorListOf(m_graph, v));
            m_predecessorMap.put(v, neighbors);
        }
        return neighbors;
    }

    private Neighbors getSuccessors(V v)
    {
        Neighbors neighbors = m_successorMap.get(v);
        if (neighbors == null) {
            neighbors = new Neighbors<V, E>(v,
                    GraphHelper.successorListOf(m_graph, v));
            m_successorMap.put(v, neighbors);
        }
        return neighbors;
    }

}