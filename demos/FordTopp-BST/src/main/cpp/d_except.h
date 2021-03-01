#ifndef EXCEPTION_CLASSES
#define EXCEPTION_CLASSES

#include <sstream>
#include <string>



class baseException
{
public:
  baseException(const std::string& str = ""):
    msgString(str)
  {
    if (msgString == "")
      msgString = "Unspecified exception";
  }
  
  std::string what() const
  {
    return msgString;
  }
  
  // protected allows a derived class to access msgString.
  // chapter 13 discusses protected in detail
protected:
  std::string msgString;
};

// failure to allocate memory (new() returns NULL)
class memoryAllocationError: public baseException
{
public:
  memoryAllocationError(const std::string& msg = ""):
    baseException(msg)
  {}
};

// function argument out of proper range
class rangeError: public baseException
{
public:
  rangeError(const std::string& msg = ""):
    baseException(msg)
  {}
};

// index out of range
class indexRangeError: public baseException
{
public:
  indexRangeError(const std::string& msg, int i, int size):
    baseException()
  {
    std::ostringstream indexErr;
    
    indexErr << msg << "  index " << i << "  size = " << size;
    // indexRangeError can modify msgString, since it is in
    // the protected section of baseException
    msgString = indexErr.str();
  }
};

// attempt to erase from an empty container
class underflowError: public baseException
{
public:
  underflowError(const std::string& msg = ""):
    baseException(msg)
  {}
};

// attempt to insert into a full container
class overflowError: public baseException
{
public:
  overflowError(const std::string& msg = ""):
    baseException(msg)
  {}
};

// error in expression evaluation
class expressionError: public baseException
{
public:
  expressionError(const std::string& msg = ""):
    baseException(msg)
  {}
};

// bad object reference
class referenceError: public baseException
{
public:
  referenceError(const std::string& msg = ""):
    baseException(msg)
  {}
};

// feature not implemented
class notImplementedError: public baseException
{
public:
  notImplementedError(const std::string& msg = ""):
    baseException(msg)
  {}
};

// date errors
class dateError: public baseException
{
public:
  dateError(const std::string& first, int v, const std::string& last):
    baseException()
  {
    std::ostringstream dateErr;
    
    dateErr << first << ' ' << v << ' ' << last;
    // dateError can modify msgString, since it is in
    // the protected section of baseException
    msgString = dateErr.str();
  }
};

// error in graph class
class graphError: public baseException
{
public:
  graphError(const std::string& msg = ""):
    baseException(msg)
  {}
};

// file open error
class fileOpenError: public baseException
{
public:
  fileOpenError(const std::string& fname):
    baseException()
  {
    std::ostringstream fileErr;
    
    fileErr << "Cannot open \"" << fname << "\"";
    // fileOpenError can modify msgString, since it is in
    // the protected section of baseException
    msgString = fileErr.str();
  }
};

// error in graph class
class fileError: public baseException
{
public:
  fileError(const std::string& msg = ""):
    baseException(msg)
  {}
};

#endif	// EXCEPTION_CLASSES
