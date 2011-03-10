package de.unisaarland.cs.st.reposuite.clustering;



public class MultilevelPartitioningTest {
	
	//	private class TestScoreVisitor implements MultilevelPartitioningScoreVisitor<Integer>,
	//	MultilevelPartitioningCollapseVisitor<Integer> {
	//		
	//		private int counter = 0;
	//		
	//		@Override
	//		public double getScore(final Integer t1, final Integer t2, final double oldScore) {
	//			return (++this.counter);
	//		}
	//		
	//		@Override
	//		public double newScore(final VirtualT<Integer> vt1, final VirtualT<Integer> vt2) {
	//			return (++this.counter);
	//		}
	//		
	//	}
	//	
	//	private TestScoreVisitor visitor;
	//	
	//	@Test
	//	public void comparatorTest(){
	//		ComparableTuple<Double, Integer> t1 = new ComparableTuple<Double, Integer>(1d, 1);
	//		ComparableTuple<Double, Integer> t2 = new ComparableTuple<Double, Integer>(2d, 0);
	//		
	//		assert (t1.compareTo(t2) > 0);
	//		
	//	}
	//	
	//	@Before
	//	public void setUp() {
	//		this.visitor = new TestScoreVisitor();
	//	}
	//	
	//	@Test
	//	public void simpleTest(){
	//		Integer[] nodes = { 1, 2, 3, 4, 5, 6 };
	//		List<MultilevelPartitioningScoreVisitor<Integer>> l = new ArrayList<MultilevelPartitioningScoreVisitor<Integer>>(
	//				1);
	//		l.add(this.visitor);
	//		MultilevelPartitioning<Integer> mp = new MultilevelPartitioning<Integer>(nodes, l, this.visitor);
	//		
	//		Set<Set<Integer>> partitions = mp.getPartitions(3);
	//		assertEquals(3, partitions.size());
	//		
	//		int oneCount = 0;
	//		int threeCount = 0;
	//
	//		for (Set<Integer> set : partitions) {
	//			if (set.size() == 1) {
	//				++oneCount;
	//			} else if (set.size() == 4) {
	//				++threeCount;
	//			} else {
	//				fail();
	//			}
	//		}
	//		
	//		assertEquals(2, oneCount);
	//		assertEquals(1, threeCount);
	//
	//	}
	//	
	//	@After
	//	public void tearDown() {
	//		
	//	}
	
}
