# Implementation-of-Sorted-Sequence-ADT-using-Linked-List-for-ApptBook

• Implemented a "sorted sequence" ADT using a linked-list
• Re-implemented the ApptBook class with a different interface and a change in the data structure.
• Created a private static nested class called "Node" inside the ApptBook class, which consists of two fields: data (Appointment
type) and the next node.
• Omitted the "tail" field in the data structure to reduce size and complexity.
• Represented "no current element" condition by having the cursor as null and the precursor as the last node of the list.
• Implemented the wellFormed method to check the data structure's validity and satisfy given invariant tests.
• Cloning the list required manual copying of cells and updating pointers to appropriate copied nodes.
• The invariant for the implementation includes:
• The list must not have any cycles where the next link points back to an earlier node.
• Data in the list must not be null and should be in non-decreasing natural order, allowing duplicates.
• The precursor field can be null or point to a node in the list, but not to a node that no longer exists.
• The cursor should be the first node if the precursor is null, or the node after the precursor otherwise.
• The "manyNodes" field accurately represents the number of nodes in the list.
• Provided functionality tests in the TestApptBook.java file and invariant checker tests in the TestInvariant.java file.
• Utilized the homework4.jar JAR file containing other ADTs and random testing for this Project
