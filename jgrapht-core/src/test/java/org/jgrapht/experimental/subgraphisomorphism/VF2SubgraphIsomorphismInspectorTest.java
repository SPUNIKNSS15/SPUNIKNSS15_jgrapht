/**
 * 
 */
package org.jgrapht.experimental.subgraphisomorphism;

import static org.junit.Assert.*;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Fabian Späh
 *
 */
public class VF2SubgraphIsomorphismInspectorTest {

	@Test
	public void testSingleMatching() {
		/*
		 *   v1 ---> v2 <---> v3 ---> v4     v5
		 *   
		 */
		DirectedGraph<String, DefaultEdge> g1 =
            new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		
		String v1 = "v1",
			   v2 = "v2",
			   v3 = "v3",
			   v4 = "v4",
			   v5 = "v5";
		
		g1.addVertex(v1);
		g1.addVertex(v2);
		g1.addVertex(v3);
		g1.addVertex(v4);
		g1.addVertex(v5);
		
		g1.addEdge(v1, v2);
		g1.addEdge(v2, v3);
		g1.addEdge(v3, v2);
		g1.addEdge(v3, v4);
		
		
		/*
		 *   v6 <---> v7 <--- v8
		 *   
		 */
		DirectedGraph<String, DefaultEdge> g2 =
			new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		
		String v6 = "v6",
			   v7 = "v7",
			   v8 = "v8";
		
		g2.addVertex(v6);
		g2.addVertex(v7);
		g2.addVertex(v8);
		
		g2.addEdge(v7, v6);
		g2.addEdge(v8, v7);
		g2.addEdge(v6, v7);
		
		
		
		VF2SubgraphIsomorphismInspector<String, DefaultEdge> vf2 =
			new VF2SubgraphIsomorphismInspector<String, DefaultEdge>(g1, g2);
		
		SubgraphIsomorphismRelation<String, DefaultEdge> rel = vf2.next();
		assertEquals("[v1=v8 v2=v7 v3=v6 v4=~~ v5=~~]", rel.toString());
	}
	
	/**
	 * Tests edge cases: Both graphs empty, second graph empty and first
	 * graph empty, graph with single node, graph with nodes but no edges
	 */
	@Test
	public void testEdgeCasesDirectedGraph() {
	    /*
	     * Tests with empty graphs and graph without edges
	     */
	    /* directed graphs */
        /* graph and subgraph empty */
        DirectedGraph<String, DefaultEdge> g1 =
                new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class),
                g2 =
                        new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

        VF2SubgraphIsomorphismInspector<String, DefaultEdge> vf2 =
                new VF2SubgraphIsomorphismInspector<String, DefaultEdge>(g1, g2);

        assertEquals("[]", vf2.next().toString());

        /* graph non-empty, subgraph empty */
        DirectedGraph<Integer, DefaultEdge> g3 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class),
                                            g4 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g3.addVertex(1);
        g3.addVertex(2);
        g3.addVertex(3);
        g3.addVertex(4);

        g3.addEdge(1, 2);
        g3.addEdge(2, 3);
        g3.addEdge(3, 4);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf3 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g3, g4);

        assertEquals("[1=~~ 2=~~ 3=~~ 4=~~]", vf3.next().toString());

        /* graph empty, subgraph non-empty */
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf4 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g4, g3);

        assertEquals(false, vf4.hasNext());


        /* graph non-empty, subgraph single vertex */
        DirectedGraph<Integer, DefaultEdge> g5 =
                new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g5.addVertex(5);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf5 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g3, g5);

        assertEquals("[1=5 2=~~ 3=~~ 4=~~]", vf5.next().toString());


        /* graph empty, subgraph single vertex */
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf5b =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g4, g5);

        assertEquals(false, vf5b.hasNext());


        /* subgraph with vertices, but no edges */
        DirectedGraph<Integer, DefaultEdge> g6 =
                new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g6.addVertex(5);
        g6.addVertex(6);
        g6.addVertex(7);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf6 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g4, g6);

        assertEquals(false, vf6.hasNext());

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf6b =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g3, g6);

        assertEquals(false, vf6b.hasNext());


        /* graph no edges, subgraph contains edge */
        DirectedGraph<Integer, DefaultEdge> g7 =
                new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g7.addVertex(5);
        g7.addVertex(6);

        g7.addEdge(5, 6);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf7 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g6, g7);

        assertEquals(false, vf7.hasNext());


        /* complete graphs of different size */
        DirectedGraph<Integer, DefaultEdge> g8 =
                new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g8.addVertex(1);
        g8.addVertex(2);
        g8.addVertex(3);
        g8.addVertex(4);
        g8.addVertex(5);

        g8.addEdge(1,2);
        g8.addEdge(1,3);
        g8.addEdge(1,4);
        g8.addEdge(1,5);
        g8.addEdge(2,3);
        g8.addEdge(2,4);
        g8.addEdge(2,5);
        g8.addEdge(3,4);
        g8.addEdge(3,5);
        g8.addEdge(4,5);

        DirectedGraph<Integer, DefaultEdge> g9 =
                new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g9.addVertex(6);
        g9.addVertex(7);
        g9.addVertex(8);
        g9.addVertex(9);

        g9.addEdge(6,7);
        g9.addEdge(6,8);
        g9.addEdge(6,9);
        g9.addEdge(7,8);
        g9.addEdge(7,9);
        g9.addEdge(8,9);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf8 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g8, g9);

        assertEquals("[1=6 2=7 3=8 4=9 5=~~]", vf8.next().toString());

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf9 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g9, g8);

        assertEquals(false, vf9.hasNext());

        /* complete graphs of same size */
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf10 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g8, g8);

        assertEquals("[1=1 2=2 3=3 4=4 5=5]", vf10.next().toString());


                /* complete graphs of different size */
        DirectedGraph<Integer, DefaultEdge> g11 =
                new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g11.addVertex(0);
        g11.addVertex(1);
        g11.addVertex(2);
        g11.addVertex(3);
        g11.addVertex(4);
        g11.addVertex(5);

        g11.addEdge(1,2);
        g11.addEdge(2,3);
        g11.addEdge(3,1);
        g11.addEdge(4, 5);


        DirectedGraph<Integer, DefaultEdge> g12 =
                new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g12.addVertex(6);
        g12.addVertex(7);
        g12.addVertex(8);
        g12.addVertex(9);
        g12.addVertex(10);

        g12.addEdge(7,6);
        g12.addEdge(9,8);
        g12.addEdge(10,9);
        g12.addEdge(8,10);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf13 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g11, g12);

        assertEquals("[0=~~ 1=8 2=10 3=9 4=7 5=6]", vf13.next().toString());



    }

    /**
    * Tests edge cases: Both graphs empty, second graph empty and first
    * graph empty, graph with single node, graph with nodes but no edges
    */
    @Test
    public void testEdgeCasesSimpleGraph()    {


        /* simple graphs */
        /* graph and subgraph empty */
        SimpleGraph<Integer, DefaultEdge> sg1 =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class),
                                          sg2 =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs2 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg1, sg2);

        assertEquals("[]", vfs2.next().toString());

        /* graph non-empty, subgraph empty */
        SimpleGraph<Integer, DefaultEdge> sg3 =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class),
                                         sg4 =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg3.addVertex(1);
        sg3.addVertex(2);
        sg3.addVertex(3);
        sg3.addVertex(4);

        sg3.addEdge(1, 2);
        sg3.addEdge(3, 2);
        sg3.addEdge(3, 4);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs3 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg3, sg4);

        assertEquals("[1=~~ 2=~~ 3=~~ 4=~~]", vfs3.next().toString());


       /* graph empty, subgraph non-empty */
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs4 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg4, sg3);

        assertEquals(false, vfs4.hasNext());

        /* graph non-empty, subgraph single vertex */
        SimpleGraph<Integer, DefaultEdge> sg5 =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg5.addVertex(5);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs5 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg3, sg5);

        assertEquals("[1=5 2=~~ 3=~~ 4=~~]", vfs5.next().toString());


        /* graph empty, subgraph single vertex */
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs5b =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg4, sg5);

        assertEquals(false, vfs5b.hasNext());


        /* subgraph with vertices, but no edges */
        SimpleGraph<Integer, DefaultEdge> sg6 =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg6.addVertex(5);
        sg6.addVertex(6);
        sg6.addVertex(7);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs6 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg4, sg6);

        assertEquals(false, vfs6.hasNext());

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs6b =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg3, sg6);

        assertEquals(false, vfs6b.hasNext());


        /* graph no edges, subgraph contains edge */
        SimpleGraph<Integer, DefaultEdge> sg7 =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg7.addVertex(5);
        sg7.addVertex(6);

        sg7.addEdge(5, 6);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs7 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg6, sg7);

        assertEquals(false, vfs7.hasNext());


        /* complete graphs of different size */
        SimpleGraph<Integer, DefaultEdge> sg8 =
                    new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg8.addVertex(1);
        sg8.addVertex(2);
        sg8.addVertex(3);
        sg8.addVertex(4);
        sg8.addVertex(5);

        sg8.addEdge(1,2);
        sg8.addEdge(1,3);
        sg8.addEdge(1,4);
        sg8.addEdge(1,5);
        sg8.addEdge(2,3);
        sg8.addEdge(2,4);
        sg8.addEdge(2,5);
        sg8.addEdge(3,4);
        sg8.addEdge(3,5);
        sg8.addEdge(4,5);

        SimpleGraph<Integer, DefaultEdge> sg9 =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg9.addVertex(6);
        sg9.addVertex(7);
        sg9.addVertex(8);
        sg9.addVertex(9);

        sg9.addEdge(6,7);
        sg9.addEdge(6,8);
        sg9.addEdge(6,9);
        sg9.addEdge(7,8);
        sg9.addEdge(7,9);
        sg9.addEdge(8,9);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs8 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg8, sg9);

        assertEquals("[1=6 2=7 3=8 4=9 5=~~]", vfs8.next().toString());

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs9 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg9, sg8);

        assertEquals(false, vfs9.hasNext());

        /* complete graphs of same size */
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs10 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg8, sg8);

        assertEquals("[1=1 2=2 3=3 4=4 5=5]", vfs10.next().toString());


    }
	
	@Rule
	public ExpectedException thrown  = ExpectedException.none();
	
	@Test
	public void testExceptions()   {
	    DirectedGraph<Integer, DefaultEdge> g1 = 
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);
	    
	    UndirectedGraph<Integer, DefaultEdge> g2 =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
	    
	    thrown.expect(IllegalArgumentException.class);
	    @SuppressWarnings("unused")
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf2 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g1, g2);
	}

    @Test
    public void testExhaustive() {
        
        /*
         * 
         *      0   3
         *      |  /|        0 2
         * g1 = | 2 |   g2 = |/
         *      |/  |        1
         *      1   4
         */
        
        SimpleGraph<Integer, DefaultEdge> g1 =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class),
                                         g2 = 
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g1.addVertex(0);
        g1.addVertex(1);
        g1.addVertex(2);
        g1.addVertex(3);
        g1.addVertex(4);
        
        g2.addVertex(0);
        g2.addVertex(1);
        g2.addVertex(2);
        
        g1.addEdge(0, 1);
        g1.addEdge(1, 2);
        g1.addEdge(2, 3);
        g1.addEdge(3, 4);

        g2.addEdge(0, 1);
        g2.addEdge(1, 2);
        
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf2 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g1, g2);
        
        assertEquals(true,
            SubgraphIsomorphismTestUtils.containsAllMatchings(vf2, g1, g2));

        
        
        /*
         * g3 = ...   g4 = ...
         * 
         */
        
        DirectedGraph<Integer, DefaultEdge> g3 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class),
                                            g4 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g3.addVertex(0);
        g3.addVertex(1);
        g3.addVertex(2);
        g3.addVertex(3);
        g3.addVertex(4);
        g3.addVertex(5);
        
        g4.addVertex(0);
        g4.addVertex(1);
        g4.addVertex(2);
        g4.addVertex(3);
        
        g3.addEdge(0, 1);
        g3.addEdge(0, 5);
        g3.addEdge(1, 4);
        g3.addEdge(2, 1);
        g3.addEdge(2, 4);
        g3.addEdge(3, 1);
        g3.addEdge(4, 0);
        g3.addEdge(5, 2);
        g3.addEdge(5, 4);
        
        g4.addEdge(0, 3);
        g4.addEdge(1, 2);
        g4.addEdge(1, 3);
        g4.addEdge(2, 3);
        g4.addEdge(2, 0);
        
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf3 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g3, g4);
        
        assertEquals(true,
            SubgraphIsomorphismTestUtils.containsAllMatchings(vf3, g3, g4));
        
        
        /*
         *      1----0        0---2
         *      |             |  /
         * g5 = |        g6 = | /
         *      |             |/
         *      2----3        1
         */
        
        SimpleGraph<Integer, DefaultEdge> g5 =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class),
                                          g6 =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
        
        g5.addVertex(0);
        g5.addVertex(1);
        g5.addVertex(2);
        g5.addVertex(3);
        
        g6.addVertex(0);
        g6.addVertex(1);
        g6.addVertex(2);

        g5.addEdge(0, 1);
        g5.addEdge(1, 2);
        g5.addEdge(2, 3);

        g6.addEdge(0, 1);
        g6.addEdge(1, 2);
        g6.addEdge(2, 0);
        
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf4 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g5, g6);
        
        assertEquals(true,
            SubgraphIsomorphismTestUtils.containsAllMatchings(vf4, g5, g6));
    }
    
    /**
     * Tests if a all given matchings are correct (on some random graphs).
     */
    @Test
    public void testRandomGraphs() {
        Random rnd = new Random();
        rnd.setSeed(54321);
        
        for (int i = 1; i < 50; i++)    {
            int vertexCount    = 2 + rnd.nextInt(i),
                edgeCount      = vertexCount + 
                    rnd.nextInt(vertexCount * (vertexCount - 1)) / 2,
                subVertexCount = 1 + rnd.nextInt(vertexCount);
            
            DirectedGraph<Integer, DefaultEdge> g1 =
                SubgraphIsomorphismTestUtils.randomGraph(vertexCount,
                                edgeCount, i),
                                                g2 =
                SubgraphIsomorphismTestUtils.randomSubgraph(g1, subVertexCount,
                                i);
            
            VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf2 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g1, g2);

            SubgraphIsomorphismTestUtils.showLog(i + ": " + vertexCount +
                            "v, " + edgeCount + "e ");
            
            while (vf2.hasNext())   {
                assertEquals(true,
                    SubgraphIsomorphismTestUtils.isCorrectMatching(vf2.next(),
                                    g1, g2));
                SubgraphIsomorphismTestUtils.showLog(".");
            }
            SubgraphIsomorphismTestUtils.showLog("\n");
        }
    }
    
    /**
     * Tests if all given matchings are correct and if every matching is found
     * (on random graphs).
     */
    @Test
    public void testRandomGraphsExhaustive() {
        Random rnd = new Random();
        rnd.setSeed(12345);
        
        for (int i = 1; i < 100; i++)    {
            int vertexCount    = 3 + rnd.nextInt(5),
                edgeCount      = rnd.nextInt(vertexCount * (vertexCount - 1)),
                subVertexCount = 2 + rnd.nextInt(vertexCount),
                subEdgeCount   = rnd.nextInt(subVertexCount * 
                                    (subVertexCount - 1));
            
            DirectedGraph<Integer, DefaultEdge> g1 =
                SubgraphIsomorphismTestUtils.randomGraph(vertexCount,
                                edgeCount, i),
                                                g2 =
                SubgraphIsomorphismTestUtils.randomGraph(subVertexCount,
                                subEdgeCount, i);
            
            VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf2 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g1,
                                g2);

            SubgraphIsomorphismTestUtils.showLog(i + ": " + vertexCount +
                            "v, " + edgeCount + "e ....\n");

            assertEquals(true,
                SubgraphIsomorphismTestUtils.containsAllMatchings(vf2, g1, g2));
        }
    }
}
