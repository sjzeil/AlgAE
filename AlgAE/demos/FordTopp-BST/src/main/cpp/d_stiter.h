#ifndef BINARY_SEARCH_TREE_ITERATOR
#define BINARY_SEARCH_TREE_ITERATOR

#include "d_stnode.h"

template <typename T>
class stree;

template <typename T>
class stiterator
{
	public:

		// constructor
		stiterator ()
		{}

		// comparison operators. just compare node pointers
		bool operator== (const stiterator& rhs) const
		{
			return nodePtr == rhs.nodePtr;
		}

		bool operator!= (const stiterator& rhs) const
		{
			return nodePtr != rhs.nodePtr;
		}

		// dereference operator. return a reference to
		// the value pointed to by nodePtr
		T& operator* () const
		{
			if (nodePtr == NULL)
 				throw
					referenceError("stree iterator operator* (): NULL reference");

			return nodePtr->nodeValue;
		}

		// preincrement. move forward to next larger value
		stiterator& operator++ ()
		{
			stnode<T> *p;

			if (nodePtr == NULL)
			{
				// ++ from end(). get the root of the tree
				nodePtr = tree->root;

				// error! ++ requested for an empty tree
				if (nodePtr == NULL)
					throw
						underflowError("stree iterator operator++ (): tree empty");

				// move to the smallest value in the tree,
				// which is the first node inorder
				while (nodePtr->left != NULL)
					nodePtr = nodePtr->left;
			}
			else
			if (nodePtr->right != NULL)
			{
				// successor is the furthest left node of
				// right subtree
				nodePtr = nodePtr->right;

				while (nodePtr->left != NULL)
					nodePtr = nodePtr->left;
			}
			else
			{
				// have already processed the left subtree, and
				// there is no right subtree. move up the tree,
				// looking for a parent for which nodePtr is a left child,
				// stopping if the parent becomes NULL. a non-NULL parent
				// is the successor. if parent is NULL, the original node
				// was the last node inorder, and its successor
				// is the end of the list
				p = nodePtr->parent;

				while (p != NULL && nodePtr == p->right)
				{
					nodePtr = p;
					p = p->parent;
				}

				// if we were previously at the right-most node in
				// the tree, nodePtr = NULL, and the iterator specifies
				// the end of the list
				nodePtr = p;
			}

			return *this;
		}

		// postincrement
		stiterator operator++ (int)
		{
			// save current iterator
			stiterator tmp = *this;

			// move myself forward to the next tree node
			++*this;

			// return the previous value
			return tmp;
		}

		// predecrement. move backward to largest value < current value
		stiterator& operator-- ()
		{
			stnode<T> *p;

			if (nodePtr == NULL)
			{
				// -- from end(). get the root of the tree
				nodePtr = tree->root;

				// error! -- requested for an empty tree
				if (nodePtr == NULL)
					throw
						underflowError("stree iterator operator--: tree empty");

				// move to the largest value in the tree,
				// which is the last node inorder
				while (nodePtr->right != NULL)
					nodePtr = nodePtr->right;
			} else if (nodePtr->left != NULL)
			{
				// must have gotten here by processing all the nodes
				// on the left branch. predecessor is the farthest
				// right node of the left subtree
				nodePtr = nodePtr->left;

				while (nodePtr->right != NULL)
					nodePtr = nodePtr->right;
			}
			else
			{
				// must have gotten here by going right and then
				// far left. move up the tree, looking for a parent
				// for which nodePtr is a right child, stopping if the
				// parent becomes NULL. a non-NULL parent is the
				// predecessor. if parent is NULL, the original node
				// was the first node inorder, and its predecessor
				// is the end of the list
				p = nodePtr->parent;
				while (p != NULL && nodePtr == p->left)
				{
					nodePtr = p;
					p = p->parent;
				}

				// if we were previously at the left-most node in
				// the tree, nodePtr = NULL, and the iterator specifies
				// the end of the list
				nodePtr = p;
			}

			return *this;
		}

		// postdecrement
		stiterator operator-- (int)
		{
			// save current iterator
			stiterator tmp = *this;

			// move myself backward to the previous tree node
			--*this;

			// return the previous value
			return tmp;
		}

		// used to construct an iterator return value from
		// an stnode pointer
		stiterator (stnode<T> *p, streeBase<T> *t) : nodePtr(p), tree(t)
		{}

	private:

		// nodePtr is the current location in the tree. we can move
		// freely about the tree using left, right, and parent.
		// tree is the address of the stree object associated
		// with this iterator. it is used only to access the
		// root pointer, which is needed for ++ and --
		// when the iterator value is end()
		stnode<T> *nodePtr;
		streeBase<T> *tree;

		friend class stree<T>;

};


template <typename T>
class const_stiterator
{
	public:

		// constructor
		const_stiterator ()
		{}

		// used to convert a const iterator to a const_iterator
		const_stiterator (const stiterator<T>& pos): nodePtr(pos.nodePtr)
		{}

		// comparison operators. just compare node pointers
		bool operator== (const const_stiterator& rhs) const
		{
			return nodePtr == rhs.nodePtr;
		}

		bool operator!= (const const_stiterator& rhs) const
		{
			return nodePtr != rhs.nodePtr;
		}

		// dereference operator. return a reference to
		// the value pointed to by nodePtr
		const T& operator* () const
		{
			if (nodePtr == NULL)
 				throw
					referenceError("stree const_iterator operator* (): NULL reference");

			return nodePtr->nodeValue;
		}

		// preincrement. move forward to next larger value
		const_stiterator& operator++ ()
		{
			stnode<T> *p;

			if (nodePtr == NULL)
			{
				// ++ from end(). get the root of the tree
				nodePtr = tree->root;

				// error! ++ requested for an empty tree
				if (nodePtr == NULL)
					throw underflowError("stree const_iterator operator++ (): tree empty");

				// move to the smallest value in the tree,
				// which is the first node inorder
				while (nodePtr->left != NULL)
					nodePtr = nodePtr->left;
			}
			else
			if (nodePtr->right != NULL)
			{
				// successor is the furthest left node of
				// right subtree
				nodePtr = nodePtr->right;

				while (nodePtr->left != NULL)
					nodePtr = nodePtr->left;
			}
			else
			{
				// have already processed the left subtree, and
				// there is no right subtree. move up the tree,
				// looking for a parent for which nodePtr is a left child,
				// stopping if the parent becomes NULL. a non-NULL parent
				// is the successor. if parent is NULL, the original node
				// was the last node inorder, and its successor
				// is the end of the list
				p = nodePtr->parent;

				while (p != NULL && nodePtr == p->right)
				{
					nodePtr = p;
					p = p->parent;
				}

				// if we were previously at the right-most node in
				// the tree, nodePtr = NULL, and the iterator specifies
				// the end of the list
				nodePtr = p;
			}

			return *this;
		}

		// postincrement
		const_stiterator operator++ (int)
		{
			// save current const_iterator
			const_stiterator tmp = *this;

			// move myself forward to the next tree node
			++*this;

			// return the previous value
			return tmp;
		}

		// predecrement. move backward to largest value < current value
		const_stiterator& operator-- ()
		{
			stnode<T> *p;

			if (nodePtr == NULL)
			{
				// -- from end(). get the root of the tree
				nodePtr = tree->root;

				// error! -- requested for an empty tree
				if (nodePtr == NULL)
					throw
						underflowError("stree iterator operator--: tree empty");

				// move to the largest value in the tree,
				// which is the last node inorder
				while (nodePtr->right != NULL)
					nodePtr = nodePtr->right;
			} else if (nodePtr->left != NULL)
			{
				// must have gotten here by processing all the nodes
				// on the left branch. predecessor is the farthest
				// right node of the left subtree
				nodePtr = nodePtr->left;

				while (nodePtr->right != NULL)
					nodePtr = nodePtr->right;
			}
			else
			{
				// must have gotten here by going right and then
				// far left. move up the tree, looking for a parent
				// for which nodePtr is a right child, stopping if the
				// parent becomes NULL. a non-NULL parent is the
				// predecessor. if parent is NULL, the original node
				// was the first node inorder, and its predecessor
				// is the end of the list
				p = nodePtr->parent;
				while (p != NULL && nodePtr == p->left)
				{
					nodePtr = p;
					p = p->parent;
				}

				// if we were previously at the left-most node in
				// the tree, nodePtr = NULL, and the iterator specifies
				// the end of the list
				nodePtr = p;
			}

			return *this;
		}

		// postdecrement
		const_stiterator operator-- (int)
		{
			// save current const_iterator
			const_stiterator tmp = *this;

			// move myself backward to the previous tree node
			--*this;

			// return the previous value
			return tmp;
		}

		// used to construct a const_iterator return value from
		// an stnode pointer
		const_stiterator (const stnode<T> *p, const streeBase<T> *t) : nodePtr(p), tree(t)
		{}

	private:

		// nodePtr is the current location in the tree. we can move
		// freely about the tree using left, right, and parent.
		// tree is the address of the stree object associated
		// with this iterator. it is used only to access the
		// root pointer, which is needed for ++ and --
		// when the iterator value is end()
		const stnode<T> *nodePtr;
		const streeBase<T> *tree;

};

#endif
