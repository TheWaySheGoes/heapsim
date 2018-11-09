package memory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * This memory model allocates memory cells based on the best-fit method.
 * 
 * @author "Johan Holmberg, Malmö university"
 * @since 1.0
 */
public class BestFit extends Memory {

	private LinkedList<PointerObject> freeList = new LinkedList<>();
	private HashMap<Pointer, Integer> allocatedSegments = new HashMap<>();

	/**
	 * Initializes an instance of a first fit-based memory.
	 *
	 * @param size The number of cells.
	 */
	public BestFit(int size) {
		super(size);
		// TODO Implement this!
		PointerObject baseSegment = new PointerObject(size, new Pointer(this));
		freeList.push(baseSegment);
	}

	/**
	 * Allocates a number of memory cells.
	 *
	 * @param allocSize the number of cells to allocate.
	 * @return The address of the first cell.
	 */
	@Override
	public Pointer alloc(int allocSize) throws NullPointerException{
		// TODO Implement this!

		try {

			PointerObject bestFitSegment;
			//Find the best fitted segment in freelist for allocation.
			try {
				bestFitSegment = freeList.stream().
						filter(segment -> segment.size >= allocSize).
						sorted(Comparator.comparing(segment -> segment.size)).
						findFirst().
						get();

			} catch (NoSuchElementException ex) {
				System.out.println("No available space");
				bestFitSegment = null;
			}

			if (bestFitSegment == null){
				return null;
			}else {

				//2 following lines for debugging
				//int pointAt = bestFitSegment.pointer.pointsAt();
				//System.out.println("BestFit Segment:" + pointAt + " - " + (pointAt + bestFitSegment.size));


				int oldStartSegmentAdress = bestFitSegment.pointer.pointsAt();
				Pointer allocPointer = new Pointer(oldStartSegmentAdress, this);

				allocatedSegments.put(allocPointer, allocSize);

				bestFitSegment.pointer.pointAt(oldStartSegmentAdress + allocSize);
				bestFitSegment.size = bestFitSegment.size - allocSize;

				return allocPointer; // return pointer pointing at the start of the old free segment.
			}

		} finally {
			removeZeroSegments();
		}

	}

	/**
	 * Releases a number of data cells
	 *
	 * @param p The pointer to release.
	 */
	@Override
	public void release(Pointer p) {
		// TODO Implement this!

		//Push segment to freeList
		int size = allocatedSegments.get(p);
		PointerObject releaseSegment = new PointerObject(size, p);
		freeList.push(releaseSegment);

		allocatedSegments.remove(p);

		//overwrite old values in memory with zeros
		int[] zeros = new int[size];
		write(p.pointsAt(), zeros);

	}

	public void removeZeroSegments(){
		for (int i = 0; i < freeList.size(); i++) {
			PointerObject segment = freeList.get(i);
			if (segment.size <= 0){
				freeList.remove(i);
			}
		}
	}

	/**
	 * Prints a simple model of the memory. Example:
	 *
	 * |    0 -  110 | Allocated
	 * |  111 -  150 | Free
	 * |  151 -  999 | Allocated
	 * | 1000 - 1024 | Free
	 */
	@Override
	public void printLayout() {
		// TODO Implement this!
		freeList.forEach(segment -> System.out.printf(
				"%-10s %4d %s %-4d size: %-4d\n",
				"Free: ",
				segment.pointer.pointsAt(),
				"-",
				(segment.pointer.pointsAt() + segment.size),
				segment.size ));

		//Comes in random order.
		allocatedSegments.forEach((pointer, size) -> System.out.printf(
				"%-10s %4d %s %-4d size: %-4d\n",
				"Allocated:",
				pointer.pointsAt(),
				"-",
				(pointer.pointsAt() + size),
				size));

	}

	@Override
	public void compact() {
		// TODO Auto-generated method stub
		
	}
}
