package memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author "Lukasz Kurasinski"
 * @since 1.0
 */
public class FirstFit2 extends Memory {

	// object describing a free memory block
	private class FreeBlock {
		int pointer;
		int size;

		public FreeBlock(int pointer, int size) {
			this.pointer = pointer;
			this.size = size;
		}

		public int getSize() {
			return size;
		}

		public int getPointer() {
			return pointer;
		}

		public void setPointer(int pointer) {
			this.pointer = pointer;
		}

		public void setSize(int size) {
			this.size = size;
		}
	}

	// those two for printing purposes mainly
	private ArrayList<Pointer> pointerTable = new ArrayList<>();
	private HashMap<Pointer, Integer> pointerSizeList = new HashMap<>();

	private LinkedList<FreeBlock> freeListTable = new LinkedList<>();

	/**
	 * Initializes an instance of a first fit-based memory. adds whole memory as a
	 * free mem block to the table
	 * 
	 * @param size
	 *            The number of cells.
	 */
	public FirstFit2(int size) {
		super(size);
		freeListTable.add(new FreeBlock(0, size));
	}

	/**
	 * @param size
	 *            the number of cells to allocate.
	 * @return The address of the first cell.
	 */
	@Override
	public Pointer alloc(int size) {

		// finds a free block big enough for the alloc
		for (int i = 0; i < freeListTable.size(); i++) {
			if (freeListTable.get(i).getSize() >= size) {
				Pointer p = new Pointer(freeListTable.get(i).getPointer(), this);
				freeListTable.get(i).setSize(freeListTable.get(i).getSize() - size);
				freeListTable.get(i).setPointer(freeListTable.get(i).getPointer() + size);

				// those two for printing mainly
				pointerTable.add(p);
				pointerSizeList.put(p, size);

				// no zero size free blocks in the freeBlockTable
				if (freeListTable.get(i).getSize() == 0) {
					freeListTable.remove(i);
				}

				// sortAll();
				return p;
			}
		}

		return null;
	}

	private void sortAll() {
		Collections.sort(pointerTable,
				(a, b) -> a.pointsAt() < b.pointsAt() ? -1 : a.pointsAt() == b.pointsAt() ? 0 : 1);
		Collections.sort(freeListTable,
				(a, b) -> a.getPointer() < b.getPointer() ? -1 : a.getPointer() == b.getPointer() ? 0 : 1);

	}

	/**
	 * Releases a number of data cells, delete data erase pointer from lists
	 * 
	 * @param p
	 *            The pointer to release.
	 */
	@Override
	public void release(Pointer p) {

		freeListTable.add(new FreeBlock(p.pointsAt(), pointerSizeList.get(p)));
		pointerTable.remove(p);
		pointerSizeList.remove(p);
		merge();
	}

	// merges free blocks if they are next to each other
	private void merge() {
		sortAll();

		for (int i = 0; i < freeListTable.size() - 1; i++) {
			if (freeListTable.get(i).getPointer() + freeListTable.get(i).getSize() == freeListTable.get(i + 1)
					.getPointer()) {
				freeListTable.add(new FreeBlock(freeListTable.get(i).getPointer(),
						freeListTable.get(i).getSize() + freeListTable.get(i + 1).getSize()));
				freeListTable.remove(i);
				freeListTable.remove(i);
				sortAll();

				i = 0;
				continue;
			}
		}
	}

	public void compact() {

	}

	@Override
	public void printLayout() {
		// sortAll();
		System.out.println(">>>>>>>>>>>empty<<<<<<<<<<<");
		for (int i = 0; i < freeListTable.size(); i++) {
			System.out.println((i) + ". points at " + freeListTable.get(i).getPointer() + " to: "
					+ (freeListTable.get(i).getPointer() + freeListTable.get(i).getSize() - 1) + " size "
					+ freeListTable.get(i).getSize());
		}
		System.out.println("<<<<<<<<<<<allocated>>>>>>>>>>>>");
		for (int i = 0; i < pointerTable.size(); i++) {
			System.out.println((i) + ". points at " + pointerTable.get(i).pointsAt() + " to: "
					+ (pointerTable.get(i).pointsAt() + pointerSizeList.get(pointerTable.get(i)) - 1) + " size "
					+ pointerSizeList.get(pointerTable.get(i)));
		}
		System.out.println("---------------------");
	}
}