package org.jgrapht.alg.isomorphism;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableUndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;


public class VF2GraphIsomorphismInspectorTest {

    @Test
    public void testAutomorphism() {
        /*
         *   v1-----v2
         *    \    /
         *     \  /
         *      v3
         */
        SimpleGraph<String, DefaultEdge> g1 =
            new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
        
        String v1 = "v1",
               v2 = "v2",
               v3 = "v3";
        
        g1.addVertex(v1);
        g1.addVertex(v2);
        g1.addVertex(v3);
        
        g1.addEdge(v1, v2);
        g1.addEdge(v2, v3);
        g1.addEdge(v3, v1);
        
        
        VF2GraphIsomorphismInspector<String, DefaultEdge> vf2 =
            new VF2GraphIsomorphismInspector<String, DefaultEdge>(g1, g1);
        
        Iterator<IsomorphicGraphMapping<String, DefaultEdge>> iter =
                        vf2.getMappings();
        
        Set<String> mappings =
            new HashSet<String>(Arrays.asList("[v1=v1 v2=v2 v3=v3]",
                                              "[v1=v1 v2=v3 v3=v2]",
                                              "[v1=v2 v2=v1 v3=v3]",
                                              "[v1=v2 v2=v3 v3=v1]",
                                              "[v1=v3 v2=v1 v3=v2]",
                                              "[v1=v3 v2=v2 v3=v1]"));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(false, iter.hasNext());
        
        
        
        /*
         *   1 ---> 2 <--- 3
         */
        DefaultDirectedGraph<Integer, DefaultEdge> g2 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);
        
        g2.addVertex(1);
        g2.addVertex(2);
        g2.addVertex(3);
        
        g2.addEdge(1, 2);
        g2.addEdge(3, 2);
        
        
        VF2GraphIsomorphismInspector<Integer, DefaultEdge> vf3 =
            new VF2GraphIsomorphismInspector<Integer, DefaultEdge>(g2, g2);
        
        Iterator<IsomorphicGraphMapping<Integer, DefaultEdge>> iter2 =
                        vf3.getMappings();
        
        Set<String> mappings2 =
            new HashSet<String>(Arrays.asList("[1=1 2=2 3=3]",
                                              "[1=3 2=2 3=1]"));
        assertEquals(true, mappings2.remove(iter2.next().toString()));
        assertEquals(true, mappings2.remove(iter2.next().toString()));
        assertEquals(false, iter2.hasNext());
    }
    
    @Test
    public void testSubgraph() {
        DirectedGraph<Integer, DefaultEdge> g1 =
            SubgraphIsomorphismTestUtils.randomGraph(10, 30, 12345);
        DirectedGraph<Integer, DefaultEdge> g2 =
            SubgraphIsomorphismTestUtils.randomSubgraph(g1, 7, 54321);
        
        VF2GraphIsomorphismInspector<Integer, DefaultEdge> vf2 =
            new VF2GraphIsomorphismInspector<Integer, DefaultEdge>(g1, g2);
        assertEquals(false, vf2.isomorphismExists());
    }

    public void testSpecialCase() {

        ListenableUndirectedGraph<Integer, DefaultEdge> gem = new ListenableUndirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);
        gem.addVertex(0);
        gem.addVertex(1);
        gem.addVertex(2);
        gem.addVertex(3);
        gem.addVertex(4);
        gem.addEdge(0, 1);
        gem.addEdge(0, 4);
        gem.addEdge(1, 2);
        gem.addEdge(1, 4);
        gem.addEdge(2, 3);
        gem.addEdge(2, 4);
        gem.addEdge(3, 4);

        ListenableUndirectedGraph<Integer, DefaultEdge> gemUK1 = new ListenableUndirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);
        gemUK1.addVertex(0);
        gemUK1.addVertex(1);
        gemUK1.addVertex(2);
        gemUK1.addVertex(3);
        gemUK1.addVertex(4);
        gemUK1.addVertex(5);
        gemUK1.addEdge(4, 2);
        gemUK1.addEdge(2, 0);
        gemUK1.addEdge(0, 1);
        gemUK1.addEdge(1, 3);
        gemUK1.addEdge(3, 4);
        gemUK1.addEdge(4, 0);
        gemUK1.addEdge(1, 4);


        /*** failed before ***/
        assertFalse(new VF2GraphIsomorphismInspector<Integer, DefaultEdge>(gemUK1, gem).isomorphismExists());

        /*** does not fail ***/
        assertFalse(new VF2GraphIsomorphismInspector<Integer, DefaultEdge>(gem, gemUK1).isomorphismExists());



        ListenableUndirectedGraph<Integer, DefaultEdge> g5 =
                new ListenableUndirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g5.addVertex(0);
        g5.addVertex(1);
        g5.addVertex(2);
        g5.addVertex(3);
        g5.addVertex(4);
        g5.addVertex(5);

        g5.addEdge(0, 1);
        g5.addEdge(1, 2);
        g5.addEdge(2, 3);
        g5.addEdge(3, 4);
        g5.addEdge(5, 2);
        g5.addEdge(5, 3);
        g5.addEdge(5, 4);

        ListenableUndirectedGraph<Integer, DefaultEdge> g6 =
                new ListenableUndirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g6.addVertex(0);
        g6.addVertex(1);
        g6.addVertex(2);
        g6.addVertex(3);
        g6.addVertex(4);
        g6.addVertex(5);

        g6.addEdge(0, 1);
//        g6.addEdge(1, 0);
        g6.addEdge(1, 2);
//        g6.addEdge(2, 1);
        g6.addEdge(2, 3);
//        g6.addEdge(3, 2);
        g6.addEdge(4, 5);
//        g6.addEdge(5, 4);
        g6.addEdge(4, 2);
//        g6.addEdge(2, 4);
        g6.addEdge(5, 2);
//        g6.addEdge(2, 5);
        g6.addEdge(5, 3);
//        g6.addEdge(3, 5);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs3 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                        (g5, g6);

        assertEquals(false, vfs3.isomorphismExists());
    }

}
