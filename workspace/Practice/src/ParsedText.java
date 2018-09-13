import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParsedText {

	public static void main(String[] args)
	{
		PDFTextStripper pdfStripper = null;
		PDDocument pdDoc = null;
		String parsedText = null;
		List<String> Results = new ArrayList<String>();
		boolean Testfail;
		try{ 
			File file = new File("C:/Users/nvardhineni_con/Downloads/EligibilityResultsNotice-9766b0be-38d4-4b0b-9769-38ad1f100690 (1).pdf"); 
			pdDoc = PDDocument.load(file);
			pdfStripper = new PDFTextStripper();
			//cosDoc = parser.getDocument();
			pdfStripper = new PDFTextStripper();

			pdfStripper.setStartPage(1);
			pdfStripper.setEndPage(1);	
			parsedText = pdfStripper.getText(pdDoc);

			if (pdDoc != null)
				pdDoc.close();
			//	System.out.println("+++++++++++++++++");
			//	System.out.println(parsedText);
			//	System.out.println("+++++++++++++++++");

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			File fXmlFile = new File("C:/Users/nvardhineni_con/Downloads/c44a3024-db1a-4377-bb35-118f358eb441 (1).xml");
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("mcu:result");
			System.out.println("----------------------------");
			System.out.println(nList.getLength());
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				//System.out.println("\nCurrent Element :"+nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					//System.out.println(eElement.getTextContent());
					Results.add(eElement.getTextContent());

				}
			}

			String parsedText1 = StringUtils.normalizeSpace(parsedText);
			//parsedText1 = parsedText1.replace((char) 160, ' ');
			parsedText1 = StringUtils.deleteWhitespace(parsedText1);

			System.out.println(parsedText1);
			for(String s : Results) { 
				System.out.println(s);
				//  s = s.replace((char) 160, ' ');
				s = StringUtils.normalizeSpace(s);
				s = StringUtils.deleteWhitespace(s);


				char[] charectersInArray = s.toCharArray();
				for(char c: charectersInArray) {
					System.out.println("["+c +"]=" + (int) c+"<<");
				}



					if(parsedText1.toString().contains(s.toString())){
						Testfail=false;
						System.out.println("pdf has the text required");
					}else {
						Testfail=true;
					}
				}
			}

			catch(Exception e)
			{
				e.printStackTrace();
				Testfail=true;

			}

		}
	}

