
package edu.uwm.cs351;

import java.util.function.Consumer;

import junit.framework.TestCase;

/******************************************************************************
 * An ApptBook ("book" for short) is a sequence of Appointment objects in sorted
 * order. The book can have a special "current element," which is specified and
 * accessed through four methods that are available in the this class (start,
 * getCurrent, advance and isCurrent).
 ******************************************************************************/
public class ApptBook implements Cloneable {

	private static class Node {
		Appointment data;
		Node next;

		public Node(Appointment data, Node next) {
			this.data = data;
			this.next = next;
		}
	}

	int manyNodes;
	Node head;
	Node cursor, precursor;

	private static Consumer<String> reporter = (s) -> {
		System.err.println("Invariant error: " + s);
	};

	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}

	private boolean wellFormed() {
		int count = 0;
		Appointment last = null;
		for (Node p = head; p != null; p = p.next) {
			if (p.data == null)
				return report("found null in the list");
			if (last != null && last.compareTo(p.data) > 0)
				return report(" Found out of order");
			++count;
			last = p.data;
		}
		if (manyNodes != count)
			return report("many nodes should be " + count + ", but was " + manyNodes);
		if (precursor != null) {
			Node p;
			for (p = head; p != precursor && p != null; p = p.next) {
				// nothing
			}
			if (p == null)
				return report("precursor not in list");
			if (precursor.next != cursor)
				return report("cursor isn't after precursor");
		} else {
			if (cursor != head)
				return report("cursorisn't null/head");
		}
		return true;
	}

	// This is only for testing the invariant.
	private ApptBook(boolean testInvariant) {
	}

	/**
	 * Initialize an empty book.
	 **/
	public ApptBook() {
		manyNodes = 0;
		head = null;
		precursor = cursor = null;

		assert wellFormed() : "invariant failed at end of constructor";
	}

	/**
	 * Determine the number of elements in this book.
	 * 
	 * @return the number of elements in this book
	 **/
	public int size() {
		assert wellFormed() : "invariant failed at start of size";
		return manyNodes;

	}

	/**
	 * Set the current element at the front of this book.
	 * 
	 * @postcondition The front element of this book is now the current element (but
	 *                if this book has no elements at all, then there is no current
	 *                element).
	 **/
	public void start() {
		assert wellFormed() : "invariant failed at start of start";
		cursor = head;
		precursor = null;
		assert wellFormed() : "invariant failed at end of start";
	}

	/**
	 * Accessor method to determine whether this book has a specified current
	 * element that can be retrieved with the getCurrent method.
	 * 
	 * @return true (there is a current element) or false (there is no current
	 *         element at the moment)
	 **/
	public boolean isCurrent() {
		assert wellFormed() : "invariant failed at start of isCurrent";
		return cursor != null;
	}

	/**
	 * Accessor method to get the current element of this book.
	 * 
	 * @precondition isCurrent() returns true.
	 * @return the current element of this book
	 * @exception IllegalStateException Indicates that there is no current element,
	 *                                  so getCurrent may not be called.
	 **/
	public Appointment getCurrent() {
		assert wellFormed() : "invariant failed at start of getCurrent";
		if (!isCurrent())
			throw new IllegalStateException("no current");
		return cursor.data;
	}

	/**
	 * Move forward, so that the current element will be the next element in this
	 * book.
	 * 
	 * @precondition isCurrent() returns true.
	 * @postcondition If the current element was already the end element of this
	 *                book (with nothing after it), then there is no longer any
	 *                current element. Otherwise, the new element is the element
	 *                immediately after the original current element.
	 * @exception IllegalStateException Indicates that there is no current element,
	 *                                  so advance may not be called.
	 **/
	public void advance() {
		assert wellFormed() : "invariant failed at start of advance";

		if (!isCurrent())
			throw new IllegalStateException("advancing past end");
		precursor = cursor;
		cursor = cursor.next;
		assert wellFormed() : "invariant failed at end of advance";
	}

	/**
	 * Remove the current element from this book.
	 * 
	 * @precondition isCurrent() returns true.
	 * @postcondition The current element has been removed from this book, and the
	 *                following element (if there is one) is now the new current
	 *                element. If there was no following element, then there is now
	 *                no current element.
	 * @exception IllegalStateException Indicates that there is no current element,
	 *                                  so removeCurrent may not be called.
	 **/
	public void removeCurrent() {
		assert wellFormed() : "invariant failed at start of removeCurrent";

		if (!isCurrent())
			throw new IllegalStateException("no current to remove");
		--manyNodes;
		cursor = cursor.next;
		if (precursor == null) {
			head = cursor;
		} else {
			precursor.next = cursor;
		}
		assert wellFormed() : "invariant failed at end of removeCurrent";
	}

	/**
	 * Set the current element to the first element that is equal or greater than
	 * the guide.
	 * 
	 * @param guide element to compare against, must not be null.
	 */
	public void setCurrent(Appointment guide) {
		assert wellFormed() : "invariant failed at start of setCurrent";
		if (guide == null)
			throw new NullPointerException("guide cannot be null");
		start();
		while (isCurrent() && getCurrent().compareTo(guide) < 0) {
			advance();
		}
		assert wellFormed() : "invariant failed at end of setCurrent";
	}

	/**
	 * Add a new element to this book, in order. If an equal appointment is already
	 * in the book, it is inserted after the last of these. The current element (if
	 * any) is not affected.
	 * 
	 * @param element the new element that is being added, must not be null
	 * @postcondition A new copy of the element has been added to this book. The
	 *                current element (whether or not is exists) is not changed.
	 * @exception IllegalArgumentException indicates the parameter is null
	 **/
	public void insert(Appointment element) {
		assert wellFormed() : "invariant failed at start of insert";
		if (element == null)
			throw new IllegalArgumentException("cannot add null");

		Node lag = null;
		Node p;
		for (p = head; p != null; p = p.next) {
			if (p.data.compareTo(element) > 0) {
				break;
			}
			lag = p;
		}
		Node newNode = new Node(element, p);
		if (lag == null) {
			head = newNode;
		} else {
			lag.next = newNode;
		}
		if (p == cursor)
			precursor = newNode;
		++manyNodes;
		assert wellFormed() : "invariant failed at end of insert";
	}

	/**
	 * Place all the appointments of another book (which may be the same book as
	 * this!) into this book in order as in {@link #insert}. The elements should
	 * added one by one from the start. The elements are probably not going to be
	 * placed in a single block.
	 * 
	 * @param addend a book whose contents will be placed into this book
	 * @precondition The parameter, addend, is not null.
	 * @postcondition The elements from addend have been placed into this book. The
	 *                current element (if any) is unchanged.
	 * @exception NullPointerException Indicates that addend is null.
	 **/
	public void insertAll(ApptBook addend) {
		assert wellFormed() : "invariant failed at start of insertAll";
		if (addend.manyNodes == 0)
			return;
		if (addend == this)
			addend = addend.clone();
		for (Node p = addend.head; p != null; p = p.next) {
			insert(p.data);
		}
		assert wellFormed() : "invariant failed at end of insertAll";
		assert addend.wellFormed() : "invariant of addend broken in insertAll";
	}

	/**
	 * Generate a copy of this book.
	 * 
	 * @return The return value is a copy of this book. Subsequent changes to the
	 *         copy will not affect the original, nor vice versa.
	 **/
	public ApptBook clone() {
		assert wellFormed() : "invariant failed at start of clone";
		ApptBook answer;

		try {
			answer = (ApptBook) super.clone();
		} catch (CloneNotSupportedException e) { // This exception should not occur. But if it does, it would probably
													// indicate a programming error that made super.clone unavailable.
													// The most common error would be forgetting the "Implements
													// Cloneable"
													// clause at the start of this class.
			throw new RuntimeException("This class does not implement Cloneable");
		}

		Node last = null;
		for (Node p = head; p != null; p = p.next) {
			Node newNode = new Node(p.data, null);
			if (p == cursor)
				answer.cursor = newNode;
			if (p == precursor)
				answer.precursor = newNode;
			if (last == null) {
				answer.head = newNode;
			} else {
				last.next = newNode;
			}
			last = newNode;
		}

		assert wellFormed() : "invariant failed at end of clone";
		assert answer.wellFormed() : "invariant on answer failed at end of clone";
		return answer;
	}

	// don't change this nested class:
	public static class TestInvariantChecker extends TestCase {
		Time now = new Time();
		Appointment e1 = new Appointment(new Period(now, Duration.HOUR), "1: think");
		Appointment e2 = new Appointment(new Period(now, Duration.DAY), "2: current");
		Appointment e3 = new Appointment(new Period(now.add(Duration.HOUR), Duration.HOUR), "3: eat");
		Appointment e4 = new Appointment(new Period(now.add(Duration.HOUR.scale(2)), Duration.HOUR.scale(8)),
				"4: sleep");
		Appointment e5 = new Appointment(new Period(now.add(Duration.DAY), Duration.DAY), "5: tomorrow");
		ApptBook hs;

		private int reports = 0;

		private void assertWellFormed(boolean expected) {
			reports = 0;
			Consumer<String> savedReporter = reporter;
			try {
				reporter = (String message) -> {
					++reports;
					if (message == null || message.trim().isEmpty()) {
						assertFalse("Uninformative report is not acceptable", true);
					}
					if (expected) {
						assertFalse("Reported error incorrectly: " + message, true);
					}
				};
				assertEquals(expected, hs.wellFormed());
				if (!expected) {
					assertEquals("Expected exactly one invariant error to be reported", 1, reports);
				}
				reporter = null;
			} finally {
				reporter = savedReporter;
			}
		}

		protected Node newNode(Appointment a, Node n) {
			Node result = new Node(a, n);
			result.data = a;
			result.next = n;
			return result;
		}

		protected void setUp() {
			hs = new ApptBook(false);
			hs.head = hs.cursor = hs.precursor = null;
			hs.manyNodes = 0;
		}

		public void testA() {
			hs.manyNodes = 1;
			assertWellFormed(false);
			hs.manyNodes = 0;
			assertWellFormed(true);
			hs.manyNodes = -1;
			assertWellFormed(false);
		}

		public void testB() {
			hs.cursor = newNode(e1, null);
			assertWellFormed(false);
			hs.precursor = newNode(null, hs.cursor);
			assertWellFormed(false);
			hs.cursor = null;
			assertWellFormed(false);
			hs.precursor.next = null;
			assertWellFormed(false);
		}

		public void testC() {
			hs.manyNodes = 1;
			hs.head = newNode(e1, null);
			assertWellFormed(false);
			hs.precursor = newNode(e1, null);
			assertWellFormed(false);
		}

		public void testD() {
			hs.manyNodes = 1;
			hs.head = newNode(null, null);
			assertWellFormed(false);
			hs.precursor = hs.head;
			assertWellFormed(false);

			hs.manyNodes = 0;
			assertWellFormed(false);
			hs.manyNodes = 2;
			assertWellFormed(false);
		}

		public void testE() {
			hs.head = newNode(e1, null);
			hs.manyNodes = 1;
			hs.precursor = hs.head;
			assertWellFormed(true);
			hs.cursor = hs.head;
			assertWellFormed(false);
			hs.precursor = null;
			assertWellFormed(true);
		}

		public void testF() {
			hs.head = hs.precursor = newNode(e1, null);
			hs.manyNodes = 1;
			hs.cursor = newNode(e1, null);
			assertWellFormed(false);
			hs.precursor = null;
			assertWellFormed(false);
			hs.cursor = hs.head;
			assertWellFormed(true);
		}

		public void testG() {
			hs.head = newNode(e2, null);
			hs.precursor = hs.head;
			hs.manyNodes = 1;
			assertWellFormed(true);

			hs.head.next = hs.head;
			assertWellFormed(false);
			hs.manyNodes = 2;
			assertWellFormed(false);
		}

		public void testH() {
			hs.head = newNode(e1, null);
			hs.precursor = newNode(e2, null);
			hs.manyNodes = 2;
			assertWellFormed(false);
			hs.head.next = hs.precursor;
			assertWellFormed(true);

			hs.manyNodes = 1;
			assertWellFormed(false);
			hs.manyNodes = 3;
			assertWellFormed(false);
			hs.manyNodes = 0;
			assertWellFormed(false);
		}

		public void testI() {
			hs.precursor = newNode(e2, null);
			hs.head = newNode(e1, hs.precursor);
			hs.manyNodes = 2;
			assertWellFormed(true);

			hs.precursor = newNode(e1, null);
			assertWellFormed(false);
			hs.precursor = null;
			assertWellFormed(false);
			hs.precursor = hs.head;
			assertWellFormed(false);
			hs.head.next.next = hs.precursor;
			assertWellFormed(false);
			hs.manyNodes = 3;
			assertWellFormed(false);
		}

		public void testJ() {
			hs.precursor = newNode(e2, null);
			hs.head = newNode(e1, hs.precursor);
			hs.manyNodes = 2;
			assertWellFormed(true);

			hs.precursor = hs.head;
			hs.cursor = null;
			assertWellFormed(false);
			hs.cursor = hs.head;
			assertWellFormed(false);
			hs.cursor = hs.head.next;
			assertWellFormed(true);
			hs.cursor = newNode(e2, null);
			assertWellFormed(false);

			hs.precursor = null;
			hs.cursor = null;
			assertWellFormed(false);
			hs.cursor = hs.head;
			assertWellFormed(true);
			hs.cursor = hs.head.next;
			assertWellFormed(false);
			hs.cursor = newNode(e1, hs.head.next);
			assertWellFormed(false);
		}

		public void testK() {
			hs.precursor = newNode(e1, null);
			hs.head = newNode(e2, hs.precursor);
			hs.manyNodes = 2;
			assertWellFormed(false);

			hs.precursor.data = e2;
			assertWellFormed(true);

			hs.precursor.data = null;
			assertWellFormed(false);

			hs.precursor = hs.head;
			hs.cursor = hs.head.next;
			assertWellFormed(false);

			hs.precursor = null;
			hs.cursor = hs.head;
			assertWellFormed(false);

			hs.head.next.data = e3;
			hs.head.data = null;
			assertWellFormed(false);

			hs.head.data = e3;
			assertWellFormed(true);

			hs.head.next.next = hs.head;
			assertWellFormed(false);

			hs.head.next.next = hs.head.next;
			assertWellFormed(false);
		}

		public void testL() {
			hs.head = hs.precursor = newNode(e5, null);
			hs.head = newNode(e4, hs.head);
			hs.head = newNode(e3, hs.head);
			hs.head = newNode(e2, hs.head);
			hs.head = newNode(e1, hs.head);
			hs.manyNodes = 1;
			assertWellFormed(false);
			hs.manyNodes = 2;
			assertWellFormed(false);
			hs.manyNodes = 3;
			assertWellFormed(false);
			hs.manyNodes = 4;
			assertWellFormed(false);
			hs.manyNodes = 0;
			assertWellFormed(false);
			hs.manyNodes = -1;
			assertWellFormed(false);
			hs.manyNodes = 5;
			assertWellFormed(true);

			hs.precursor = newNode(e5, null);
			assertWellFormed(false);
		}

		public void testM() {
			hs.head = hs.precursor = newNode(e4, null);
			hs.head = newNode(e3, hs.head);
			hs.head = newNode(e3, hs.head);
			hs.head = newNode(e1, hs.head);
			hs.head = newNode(e1, hs.head);
			hs.manyNodes = 5;
			assertWellFormed(true);

			hs.precursor = null;
			assertWellFormed(false);
			hs.precursor = newNode(e4, null);
			assertWellFormed(false);
			hs.precursor = hs.head;
			assertWellFormed(false);
			hs.precursor = hs.head.next;
			assertWellFormed(false);
			hs.precursor = hs.head.next.next;
			assertWellFormed(false);
			hs.precursor = hs.precursor.next;
			assertWellFormed(false);
			hs.precursor = hs.precursor.next;

			assertWellFormed(true);
		}

		public void testN() {
			Node n1, n2, n3, n4, n5;
			hs.head = n5 = newNode(e5, null);
			hs.head = n4 = newNode(e4, hs.head);
			hs.head = n3 = newNode(e3, hs.head);
			hs.head = n2 = newNode(e2, hs.head);
			hs.head = n1 = newNode(e1, hs.head);
			hs.manyNodes = 5;

			hs.precursor = newNode(e1, n1);
			hs.cursor = n1;
			assertWellFormed(false);
			hs.precursor = newNode(e1, n2);
			hs.cursor = n2;
			assertWellFormed(false);
			hs.precursor = newNode(e2, n3);
			hs.cursor = n3;
			assertWellFormed(false);
			hs.precursor = newNode(e3, n4);
			hs.cursor = n4;
			assertWellFormed(false);
			hs.precursor = newNode(e4, n5);
			hs.cursor = n5;
			assertWellFormed(false);
			hs.precursor = newNode(e5, null);
			hs.cursor = null;
			assertWellFormed(false);

			hs.precursor = null;
			hs.cursor = n1;
			assertWellFormed(true);
			hs.precursor = n1;
			hs.cursor = n2;
			assertWellFormed(true);
			hs.precursor = n2;
			hs.cursor = n3;
			assertWellFormed(true);
			hs.precursor = n3;
			hs.cursor = n4;
			assertWellFormed(true);
			hs.precursor = n4;
			hs.cursor = n5;
			assertWellFormed(true);
			hs.precursor = n5;
			hs.cursor = null;
			assertWellFormed(true);
		}

		public void testO() {
			Node n1, n2, n3, n4, n5;
			hs.head = n5 = newNode(e5, null);
			hs.head = n4 = newNode(e5, hs.head);
			hs.head = n3 = newNode(e5, hs.head);
			hs.head = n2 = newNode(e5, hs.head);
			hs.head = n1 = newNode(e5, hs.head);
			hs.manyNodes = 5;

			hs.precursor = null;
			hs.cursor = n2;
			assertWellFormed(false);
			hs.cursor = n3;
			assertWellFormed(false);
			hs.cursor = n4;
			assertWellFormed(false);
			hs.cursor = n5;
			assertWellFormed(false);
			hs.cursor = null;
			assertWellFormed(false);

			hs.precursor = n1;
			hs.cursor = n1;
			assertWellFormed(false);
			hs.cursor = n3;
			assertWellFormed(false);
			hs.cursor = n4;
			assertWellFormed(false);
			hs.cursor = n5;
			assertWellFormed(false);
			hs.cursor = null;
			assertWellFormed(false);

			hs.precursor = n2;
			hs.cursor = n1;
			assertWellFormed(false);
			hs.cursor = n2;
			assertWellFormed(false);
			hs.cursor = n4;
			assertWellFormed(false);
			hs.cursor = n5;
			assertWellFormed(false);
			hs.cursor = null;
			assertWellFormed(false);

			hs.precursor = n3;
			hs.cursor = n1;
			assertWellFormed(false);
			hs.cursor = n2;
			assertWellFormed(false);
			hs.cursor = n3;
			assertWellFormed(false);
			hs.cursor = n5;
			assertWellFormed(false);
			hs.cursor = null;
			assertWellFormed(false);

			hs.precursor = n4;
			hs.cursor = n1;
			assertWellFormed(false);
			hs.cursor = n2;
			assertWellFormed(false);
			hs.cursor = n3;
			assertWellFormed(false);
			hs.cursor = n4;
			assertWellFormed(false);
			hs.cursor = null;
			assertWellFormed(false);

			hs.precursor = n5;
			hs.cursor = n1;
			assertWellFormed(false);
			hs.cursor = n2;
			assertWellFormed(false);
			hs.cursor = n3;
			assertWellFormed(false);
			hs.cursor = n4;
			assertWellFormed(false);
			hs.cursor = n5;
			assertWellFormed(false);
		}

		public void testP() {
			Node n1, n2, n3, n4, n5;
			hs.head = n5 = newNode(e5, null);
			hs.head = n4 = newNode(e4, hs.head);
			hs.head = n3 = newNode(e3, hs.head);
			hs.head = n2 = newNode(e2, hs.head);
			hs.head = n1 = newNode(e1, hs.head);
			hs.manyNodes = 5;
			hs.cursor = n1;
			assertWellFormed(true);

			n5.data = null;
			assertWellFormed(false);

			n5.data = e3;
			assertWellFormed(false);

			n4.data = e3;
			assertWellFormed(true);

			n4.data = null;
			assertWellFormed(false);

			n4.data = e2;
			assertWellFormed(false);

			n3.data = e2;
			assertWellFormed(true);

			n3.data = null;
			assertWellFormed(false);

			n3.data = e1;
			assertWellFormed(false);

			n2.data = e1;
			assertWellFormed(true);

			n2.data = null;
			assertWellFormed(false);
		}
	}
}
