package memory;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

class PointerObject{

	int size;
	Pointer pointer;

	public PointerObject(int size, Pointer pointer){
		this.size = size;
		this.pointer = pointer;
	}
}

/**
 * This memory model allocates memory cells based on the first-fit method. 
 * 
 * @author "Johan Holmberg, Malmö university"
 * @since 1.0
 */
public class FirstFit extends Memory {

	private LinkedList<PointerObject> pointerList = new LinkedList<>();
	private HashMap<Pointer, Integer> allocatedSegments = new HashMap<>();

	/**
	 * Initializes an instance of a first fit-based memory.
	 * 
	 * @param size The number of cells.
	 */
	public FirstFit(int size) {
		super(size);
		// TODO Implement this!
		PointerObject pointer = new PointerObject(size, new Pointer(this));
		pointerList.push(pointer);
	}

	/**
	 * Allocates a number of memory cells.
	 * freeList will shrink for every allocation.
	 * 
	 * @param size the number of cells to allocate.
	 * @return The address of the first cell.
	 */
	@Override
	public Pointer alloc(int size) throws NullPointerException {

		try {
			for (int i = 0; i < pointerList.size(); i++) {
				PointerObject po = pointerList.get(i);
				if (po.size >= size) {
					Pointer pointer = new Pointer(po.pointer.pointsAt(), this);
					allocatedSegments.put(pointer, size);
					po.pointer.pointAt(po.pointer.pointsAt() + size);
					po.size = po.size - size;

					return pointer; // return pointer pointing at the start of the old free segment.
				}
			}
		} finally {
			removeZeroSegments();
		}

		return null;
	}

	/**
	 * Releases a number of data cells
	 * freeList will increment by one for every deallocation.
	 *
	 * @param p The pointer to release.
	 */
	@Override
	public void release(Pointer p) {
		// TODO Implement this!

		//Push segment to freeList
		int size = allocatedSegments.get(p);
		PointerObject releaseSegment = new PointerObject(size, p);
		pointerList.push(releaseSegment);

		allocatedSegments.remove(p);

		//overwrite old values in memory with zeros
		int[] zeros = new int[size];
		write(p.pointsAt(), zeros);

	}

	/**
	 * The method removes dummy segments from freeList wich has 0 as length.
	 */
	public void removeZeroSegments(){
		for (int i = 0; i < pointerList.size(); i++) {
			PointerObject segment = pointerList.get(i);
			if (segment.size <= 0){
				pointerList.remove(i);
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
		Collections.sort(pointerList,
				(a, b) -> a.pointer.pointsAt() < b.pointer.pointsAt() ? -1 : a.pointer.pointsAt() == b.pointer.pointsAt() ? 0 : 1);
	
		pointerList.forEach(segment -> System.out.printf(
				"%-10s %4d %s %-4d size: %-4d\n",
				"Free: ",
				segment.pointer.pointsAt(),
				"-",
				(segment.pointer.pointsAt() + segment.size),
				segment.size ));

		//Comes in random order
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
