#include <time.h>
#include <iostream>
#include <cstdlib>
#include <sstream>

#include <string>
#include <typeinfo>

#include <algae/animation.h>
#include <algae/rendering/renderer.h>

#include "d_stree.h"
#include "d_stiter.h"

#include <algae/snapshot/entity.h>

using namespace std;
using namespace algae;




class BinTreeAnimation: public Animation
{
public:
	BinTreeAnimation();

	virtual void buildMenu();

	virtual string about() {
		return "Binary search code adapted for AlgAE animation by\n  Steven J. Zeil, Old Dominion University";
	}

	static stree<int> tree;
	static stree<int>::iterator current;

	static bool used[100];

	static int markUsed (int i);
	static int markUnused (int i);
	static int getUniqueRandomValue();

	static void startup();
	static void bstInsert();
	static void bstSeqInsert();
	static void bstRandomInsert();
	static void bstDelete();
	static void bstFind();
	//static void bstPreTraverse();
	//static void bstInTraverse();
	//static void bstPostTraverse();
	//static void bstLevelTraverse();
	static void bstClear();

	static void createSampleTree1();
private:

};



stree<int> BinTreeAnimation::tree;
stree<int>::iterator BinTreeAnimation::current;

bool BinTreeAnimation::used[100];


BinTreeAnimation::BinTreeAnimation()
{
}





unsigned rnd (unsigned limit)
{
  return (rand() >> 2) % limit;
}


void BinTreeAnimation::createSampleTree1()
{
	algae()->setBreakpointsEnabled (false);
	tree.erase(tree.begin(), tree.end());
	int data[] = {30, 20, 70, 10, 50, 40, 60, -1};
	for (int k = 0; data[k] >= 0; ++k) {
		tree.insert (k);
	}
	algae()->setBreakpointsEnabled (true);
}


void BinTreeAnimation::buildMenu()
{
	  registerStartingAction (startup);
	  registerAction ("Insert", bstInsert);
	  registerAction("Sequential Inserts", bstSeqInsert);
	  registerAction("Random Inserts", bstRandomInsert);
	  registerAction("Delete", bstDelete);
	  registerAction("Find", bstFind);
	  //registerAction("Pre-Order Traverse", bstPreTraverse);
	  //registerAction("In-Order Traverse", bstInTraverse);
	  //registerAction("Post-Order Traverse", bstPostTraverse);
	  //registerAction("Level-Order Traverse", bstLevelTraverse);
	  registerAction("Clear", bstClear);

}


void BinTreeAnimation::startup()
{
	fill_n (used, 100, false);
	current = tree.end();
	globalVar("tree", tree);
	globalVar("current", current);
	createSampleTree1();
}


int BinTreeAnimation::markUsed (int i)
{
  if ((i >= 0) && (i < 100))
    used[i] = true;
  return i;
}


int BinTreeAnimation::markUnused (int i)
{
  if ((i >= 0) && (i < 100))
    used[i] = false;
  return i;
}


int BinTreeAnimation::getUniqueRandomValue()
{
  int r = rnd(100);
  while (used[r])
    r = rnd(100);
  markUsed(r);
  return r;
}


void BinTreeAnimation::bstInsert()
{
  int v;
  promptForInput("Value to insert", v);
  tree.insert (markUsed(v));
}

void BinTreeAnimation::bstSeqInsert()
{
  int v, d, N;
  promptForInput("Starting value", v);
  promptForInput("Increment", d);
  promptForInput("Number of values", N);
  for (int i1 = 0; i1 < N; i1++)
    tree.insert(markUsed(v+i1*d));
}

void BinTreeAnimation::bstRandomInsert()
{
  int N;
  promptForInput("Number of values", N);
  for (int i1 = 0; i1 < N; i1++)
    tree.insert(markUsed(getUniqueRandomValue()));
}


void BinTreeAnimation::bstDelete()
{
  int v;
  promptForInput("Value to remove", v);
  tree.erase (markUnused(v));
}


void BinTreeAnimation::bstFind()
{
  int v;
  cout << "Value to find" << endl;
  cin >> v;
  cout << "Entered: " << v << endl;
  //	  promptForInput("Value to find", v);
  tree.find (v);
}




void BinTreeAnimation::bstClear()
{
	algae()->setBreakpointsEnabled (false);
	tree.erase(tree.begin(), tree.end());
	algae()->setBreakpointsEnabled (true);
}




