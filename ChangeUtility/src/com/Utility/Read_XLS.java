package com.Utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;



public class Read_XLS {	
	public  String filelocation;
	public  FileInputStream ipstr = null;
	public  FileOutputStream opstr =null;
	private HSSFWorkbook wb = null;
	private HSSFSheet ws = null;	
	
	public Read_XLS(String filelocation) {		
		this.filelocation=filelocation;
		try {
			ipstr = new FileInputStream(filelocation);
			wb = new HSSFWorkbook(ipstr);
			ws = wb.getSheetAt(0);
			ipstr.close();
		} catch (Exception e) {			
			e.printStackTrace();
		} 
		
	}
	
	//To retrieve No Of Rows from .xls file's sheets.
	public int retrieveNoOfRows(String wsName){		
		int sheetIndex=wb.getSheetIndex(wsName);
		if(sheetIndex==-1)
			return 0;
		else{
			ws = wb.getSheetAt(sheetIndex);
			int rowCount=ws.getLastRowNum()+1;		
			return rowCount;		
		}
	}
	
	//To retrieve No Of Columns from .cls file's sheets.
	public int retrieveNoOfCols(String wsName){
		int sheetIndex=wb.getSheetIndex(wsName);
		if(sheetIndex==-1)
			return 0;
		else{
			ws = wb.getSheetAt(sheetIndex);
			int colCount=ws.getRow(0).getLastCellNum();			
			return colCount;
		}
	}
	
	//To retrieve SuiteToRun and CaseToRun flag of test suite and test case.
	public String retrieveToRunFlag(String wsName, String colName, String rowName){
		
		int sheetIndex=wb.getSheetIndex(wsName);
		if(sheetIndex==-1)
			return null;
		else{
			int rowNum = retrieveNoOfRows(wsName);
			int colNum = retrieveNoOfCols(wsName);
			int colNumber=-1;
			int rowNumber=-1;			
			
			HSSFRow Suiterow = ws.getRow(0);				
			
			for(int i=0; i<colNum; i++){
				if(Suiterow.getCell(i).getStringCellValue().equals(colName.trim())){
					colNumber=i;					
				}					
			}
			
			if(colNumber==-1){
				return "";				
			}
			
			
			for(int j=0; j<rowNum; j++){
				HSSFRow Suitecol = ws.getRow(j);				
				if(Suitecol.getCell(0).getStringCellValue().equals(rowName.trim())){
					rowNumber=j;	
				}					
			}
			
			if(rowNumber==-1){
				return "";				
			}
			
			HSSFRow row = ws.getRow(rowNumber);
			HSSFCell cell = row.getCell(colNumber);
			if(cell==null){
				return "";
			}
			String value = cellToString(cell);
			return value;			
		}			
	}
	
	//To retrieve DataToRun flag of test data.
	public String[] retrieveToRunFlagTestData(String wsName, String colName, int rowNumber){
		
		int sheetIndex=wb.getSheetIndex(wsName);
		if(sheetIndex==-1)
			return null;
		else{
			int rowNum = retrieveNoOfRows(wsName);
			int colNum = retrieveNoOfCols(wsName);
			int colNumber=-1;
					
			
			
			HSSFRow Suiterow = ws.getRow(0);				
			String data[] = new String[rowNum-1];
			for(int i=0; i<colNum; i++){
				if(Suiterow.getCell(i).getStringCellValue().equals(colName.trim())){
					colNumber=i;					
				}					
			}
			
			if(colNumber==-1){
				return null;				
			}
			
			HSSFRow Row;
			for(int j=0; j<rowNum-1; j++){
				if(rowNumber == -1){
					Row = ws.getRow(j+1);
				}else {
					Row = ws.getRow(rowNumber);
				}
				
				if(Row==null){
					data[j] = "";
				}
				else{
					HSSFCell cell = Row.getCell(colNumber);
					if(cell==null){
						data[j] = "";
					}
					else{
						String value = cellToString(cell);
						data[j] = value;	
					}	
				}
			}
			return data;			
		}			
	}
	
	//To retrieve test data from test case data sheets.
	@SuppressWarnings("deprecation")
	public Object[][] retrieveTestData(String wsName, int rowNumber){
		int sheetIndex=wb.getSheetIndex(wsName);
		Object data[][] = null;
		if(sheetIndex==-1)
			return null;
		else{
				int rowNum = retrieveNoOfRows(wsName);
				int colNum = retrieveNoOfCols(wsName);
		
				if(rowNumber == -1){
					
					data= new Object[rowNum-1][colNum-5];
					
					for (int i=0; i<rowNum-1; i++){
						HSSFRow row = ws.getRow(i+1);
						for(int j=0; j< colNum-5; j++){					
							if(row==null){
								data[i][j] = "";
							}
							else{
								HSSFCell cell = row.getCell(j);	
						
								if(cell==null){
									data[i][j] = "";							
								}
								else{
									switch (cell.getCellTypeEnum()) {
					                case NUMERIC:
					                    if (DateUtil.isCellDateFormatted(cell)) {
					                        Date date = cell.getDateCellValue();
					                        String value = date.toString();
					                        data[i][j] = value;	
					                    } else {
					                    	String value = cellToString(cell);
											data[i][j] = value;		
					                    }
					                    break;
					                default:
					                	String value = cellToString(cell);
										data[i][j] = value;		
					                	break;
									}					
								}
							}
						}
					}
					
				} else {
					data = new Object[1][colNum-5];
					
						HSSFRow row = ws.getRow(rowNumber);
						for(int j=0; j< colNum-5; j++){					
							if(row==null){
								data[0][j] = "";
							}
							else{
								HSSFCell cell = row.getCell(j);	
						
								if(cell==null){
									data[0][j] = "";							
								}
								else{
									switch (cell.getCellTypeEnum()) {
					                case NUMERIC:
					                    if (DateUtil.isCellDateFormatted(cell)) {
					                        Date date = cell.getDateCellValue();
					                        String value = date.toString();
					                        data[0][j] = value;	
					                    } else {
					                    	String value = cellToString(cell);
											data[0][j] = value;		
					                    }
					                    break;
					                default:
					                	String value = cellToString(cell);
										data[0][j] = value;		
					                	break;
									}
								}
							}
						}	
						
				}
				
						
				return data;		
		}
	
	}		
	
	
	@SuppressWarnings("deprecation")
	public static String cellToString(HSSFCell cell){
		int type;
		Object result;
		type = cell.getCellType();			
		switch (type){
			case 0 :
				result = cell.getNumericCellValue();
				break;
				
			case 1 : 
				result = cell.getStringCellValue();
				break;
			case 3:
				result = cell.getStringCellValue();
				break;
			default :
				throw new RuntimeException("Unsupported cell.");			
		}
		return result.toString();
	}
	
	//To write result In test data and test case list sheet.
	@SuppressWarnings("deprecation")
	public boolean writeResult(String wsName, String colName, int rowNumber, String Result){
		try{
			int sheetIndex=wb.getSheetIndex(wsName);
			if(sheetIndex==-1)
				return false;			
			int colNum = retrieveNoOfCols(wsName);
			int colNumber=-1;
					
			
			HSSFRow Suiterow = ws.getRow(0);			
			for(int i=0; i<colNum; i++){				
				if(Suiterow.getCell(i).getStringCellValue().equals(colName.trim())){
					colNumber=i;					
				}					
			}
			
			if(colNumber==-1){
				return false;				
			}
			
			HSSFRow Row = ws.getRow(rowNumber);
			HSSFCell cell = Row.getCell(colNumber);
			if (cell == null)
		        cell = Row.createCell(colNumber);	
			
			if(Result.toString().equalsIgnoreCase("pass")){
				HSSFCellStyle style = wb.createCellStyle();
				style.setFillForegroundColor(HSSFColor.GREEN.index);
				style.setFillBackgroundColor(HSSFColor.GREEN.index);
				style.setFillPattern(CellStyle.SOLID_FOREGROUND);
				cell.setCellStyle(style);
			} else if(Result.toString().equalsIgnoreCase("fail")){
				HSSFCellStyle style = wb.createCellStyle();
				style.setFillForegroundColor(HSSFColor.RED.index);
				style.setFillBackgroundColor(HSSFColor.RED.index);
				style.setFillPattern(CellStyle.SOLID_FOREGROUND);
				cell.setCellStyle(style);
			} else if(Result.toString().equalsIgnoreCase("skip")){
				HSSFCellStyle style = wb.createCellStyle();
				style.setFillForegroundColor(HSSFColor.YELLOW.index);
				style.setFillBackgroundColor(HSSFColor.YELLOW.index);
				style.setFillPattern(CellStyle.SOLID_FOREGROUND);
				cell.setCellStyle(style);
			}
			cell.setCellValue(Result);
			
			opstr = new FileOutputStream(filelocation);
			wb.write(opstr);
			opstr.close();
			
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//To write result In test suite list sheet.
	@SuppressWarnings("deprecation")
	public boolean writeResult(String wsName, String colName, String rowName, String Result){
		try{
			int rowNum = retrieveNoOfRows(wsName);
			int rowNumber=-1;
			int sheetIndex=wb.getSheetIndex(wsName);
			if(sheetIndex==-1)
				return false;			
			int colNum = retrieveNoOfCols(wsName);
			int colNumber=-1;
					
			
			HSSFRow Suiterow = ws.getRow(0);			
			for(int i=0; i<colNum; i++){				
				if(Suiterow.getCell(i).getStringCellValue().equals(colName.trim())){
					colNumber=i;					
				}					
			}
			
			if(colNumber==-1){
				return false;				
			}
			
			for (int i=0; i<rowNum-1; i++){
				HSSFRow row = ws.getRow(i+1);				
				HSSFCell cell = row.getCell(0);	
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String value = cellToString(cell);	
				if(value.equals(rowName)){
					rowNumber=i+1;
					break;
				}
			}		
			
			HSSFRow Row = ws.getRow(rowNumber);
			HSSFCell cell = Row.getCell(colNumber);
			if (cell == null)
		        cell = Row.createCell(colNumber);			
			
			cell.setCellValue(Result);
			
			opstr = new FileOutputStream(filelocation);
			wb.write(opstr);
			opstr.close();
			
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	public boolean highlightFailingColumns(String sheetName, String columnNames, int rowNum) {
		try{
			int sheetIndex=wb.getSheetIndex(sheetName);
			if(sheetIndex==-1)
				return false;			
			int colNum = retrieveNoOfCols(sheetName);
			int colNumber=-1;
			
			HSSFRow Suiterow = ws.getRow(0);
			String [] columnList = getFailingColumnList(columnNames);
			if(columnList == null){
				for(int i=0; i<colNum; i++){		
					if(Suiterow.getCell(i).getStringCellValue().equals(columnNames.trim())){
						colNumber=i;					
					}					
				}
				
				if(colNumber==-1){
					return false;				
				}
				
				HSSFRow Row = ws.getRow(rowNum);
				HSSFCell cell = Row.getCell(colNumber);
				if (cell == null)
			        cell = Row.createCell(colNumber);	
				
				HSSFCellStyle style = wb.createCellStyle();
				style.setFillForegroundColor(HSSFColor.RED.index);
				style.setFillBackgroundColor(HSSFColor.RED.index);
				style.setFillPattern(CellStyle.SOLID_FOREGROUND);
				cell.setCellStyle(style);
				
			}else {
				int[] colNumbers = new int[columnList.length];
				for(int i=0; i<colNum; i++){
					for(int j=0; j<columnList.length; j++){
						if(Suiterow.getCell(i).getStringCellValue().equals(columnList[j].trim())){
							colNumbers[j] = i;				
						}
					}
				}
				System.out.println(colNumbers.toString());
				for(int i=0; i<colNumbers.length; i++){
					
					if(i!=-1){
						HSSFRow Row = ws.getRow(rowNum);
						HSSFCell cell = Row.getCell(i);
						if (cell == null)
					        cell = Row.createCell(i);	
						
						HSSFCellStyle style = wb.createCellStyle();
						style.setFillForegroundColor(HSSFColor.RED.index);
						style.setFillBackgroundColor(HSSFColor.RED.index);
						style.setFillPattern(CellStyle.SOLID_FOREGROUND);
						cell.setCellStyle(style);
					}
					
				}
			}
			
			opstr = new FileOutputStream(filelocation);
			wb.write(opstr);
			opstr.close();
			
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private String[] getFailingColumnList(String columnNames) {
		if(columnNames.contains(",")){
			String[] list = columnNames.split("\\,");
			return list;
		} else {
			return null;
		}
	}
}
