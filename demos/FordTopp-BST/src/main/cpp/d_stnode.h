#ifndef BINARY_SEARCH_TREE_NODE
#define BINARY_SEARCH_TREE_NODE

#ifndef NULL
#include <cstddef>
using std::NULL;
#endif  // NULL


// declares a binary search tree node object
template <typename T>
class stnode
{
   public:
		// stnode is used to implement the binary search tree class
		// making the data public simplifies building the class functions

		T nodeValue;
			// node data
		stnode<T> *left, *right, *parent;
		// child pointers and pointer to the node's parent

      // constructor
		stnode (const T& item, stnode<T> *lptr = NULL, 
              stnode<T> *rptr = NULL, stnode<T> *pptr = NULL):
				nodeValue(item), left(lptr), right(rptr), parent(pptr)
		{}
};


// declares a simple base class for search trees
template <typename T>
class streeBase
{
public:
	stnode<T> *root;
};






#endif  // BINARY_SEARCH_TREE_CLASS
