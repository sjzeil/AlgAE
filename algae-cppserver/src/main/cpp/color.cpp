#include <algae/snapshot/color.h>
#include <algae/communications/xmlOutput.h>


namespace algae {

  const Color Color::Black       (0, 0, 0);
  const Color Color::Red         (255, 0, 0);
  const Color Color::PaleRed     (255, 150, 150);
  const Color Color::Green       (0, 255, 0);
  const Color Color::PaleGreen   (150, 255, 150);
  const Color Color::Blue        (0, 0, 255);
  const Color Color::PaleBlue    (150, 150, 255);
  const Color Color::Yellow      (255, 255, 0);
  const Color Color::PaleYellow  (255, 255, 150);
  const Color Color::Magenta     (255, 0, 255);
  const Color Color::PaleMagenta (255, 150, 255);
  const Color Color::Cyan        (0, 255, 255);
  const Color Color::PaleCyan    (150, 255, 255);
  const Color Color::DarkGray    (128, 128, 128);
  const Color Color::MedGray     (192, 192, 192);
  const Color Color::LightGray   (224, 224, 224);
  const Color Color::White       (255, 255, 255);

  void Color::printXML (std::ostream& out) const
  {
	  ObjectTag obj (out, "java.awt.Color");
	  IntValue (out, red);
	  IntValue (out, green);
	  IntValue (out, blue);
	  IntValue (out, alpha);
	  obj.close();
  }


  std::ostream& operator<< (std::ostream& out, const Color& c)
  {
	  out << "[" << (int)c.red << "," << (int)c.green << "," << (int)c.blue << "," << (int)c.alpha << "]";
	  return out;
  }

}
