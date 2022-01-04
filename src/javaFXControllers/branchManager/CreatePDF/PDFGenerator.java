package javaFXControllers.branchManager.CreatePDF;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import common.Logger;
import common.Message;
import common.Logger.Level;

public class PDFGenerator {
	
	private String FILEPATH = "";
	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
	private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
	private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

	private JSONObject quarterReports;
	private String currentBranch;
	private String currentQuarter;
	private String currentYear;
	private String managerName;

	// Facts variables
	private long totalQuarterOrders = 0;
	private long totalQuarterIncome = 0;
	private long mostOrdersFromRestaurant = 0;
	private long mostIncomeFromRestaurant = 0;
	private long leastOrdersFromRestaurant = 0;
	private long leastIncomeFromRestaurant = 0;
	
	private String leastOrdersFromRestaurantName = "";
	private String leastIncomeFromRestaurantName = "";
	private String mostOrdersFromRestaurantName = "";
	private String mostIncomeFromRestaurantName = "";
	private String fileName = "";
	public void setReportsInfo(JSONObject quarterReports, String currentBranch, String currentYear,
			String currentQuarter, String managerName) {
		this.quarterReports = quarterReports;
		this.currentBranch = currentBranch;
		this.currentYear = currentYear;
		this.currentQuarter = currentQuarter;
		this.managerName = managerName;
		fileName = currentBranch + "_" + currentQuarter + "_" + currentYear + ".pdf";
		FILEPATH = "src//javaFXControllers//branchManager//CreatePDF//" + fileName;

	}
	
	public void setFilePath(String str) {
		FILEPATH = str;
	}

	public String getFileName() {
		return fileName;
	}
	
	public String getFilePath() {
		return FILEPATH;
	}

	public void createPDF() {

		try {
			// Creating the PDF File
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(FILEPATH));
			document.open(); // File is created in path
			
			addTitlePage(document);
			addIncomeContent(document, writer);
			addItemsContent(document, writer);
			addPerformanceContent(document, writer);
			
			document.close(); // File is updated in path

		} catch (Exception e) {
			System.out.println(
					"PDFGenerator: createPDF: Exception was thrown while creating the local pdf file.");
			Logger.log(Level.WARNING,
					"PDFGenerator: createPDF: Exception was thrown while creating the local pdf file.");
		}
		
		System.out.println(
				"PDFGenerator: createPDF: Temp quarterly report file was created");
	}

	public Image writeChartToPDF(JFreeChart chart, int width, int height, PdfWriter writer) {

		try {
			PdfContentByte contentByte = writer.getDirectContent();
			PdfTemplate template = contentByte.createTemplate(width, height);
			@SuppressWarnings("deprecation")
			Graphics2D graphics2d = template.createGraphics(width, height, new DefaultFontMapper());
			Rectangle2D rectangle2d = new Rectangle2D.Double(0, 0, width, height);

			chart.draw(graphics2d, rectangle2d);

			graphics2d.dispose();
			return Image.getInstance(template);
			
		} catch (Exception e) {
			System.out.println(
					"PDFGenerator: writeChartToPDF: Exception was thrown while creating chart in pdf file");
			Logger.log(Level.WARNING,
					"PDFGenerator: writeChartToPDF: Exception was thrown while creating chart in pdf file");
		}
		
		return null;
	}

	// iText allows to add metadata to the PDF which can be viewed in your Adobe
	// Reader
	// under File -> Properties

	private void addTitlePage(Document document) throws DocumentException {
		Paragraph preface = new Paragraph();
		// We add one empty line
		addEmptyLine(preface, 1);
		// Lets write a big header
		preface.add(new Paragraph("Quarterly Report - ( " + currentQuarter + ", " + currentYear + " )", catFont));

		addEmptyLine(preface, 1);
		// Will create: Report generated by: _name, _date
		preface.add(new Paragraph("Report generated by: " + managerName + "\nDate: " + new Date(), smallBold));
		preface.add(new Paragraph("Branch: " + currentBranch, smallBold));
		addEmptyLine(preface, 2);
		preface.add(new Paragraph("This report documentation describes three different kinds of report data such as:", smallBold));
		addEmptyLine(preface, 1);
		preface.add(new Paragraph("* Income report - Amount of revenue and orders count placed by all restaurants in the " + currentBranch + " branch. ", smallBold));
		preface.add(new Paragraph("* Items report - Amount of main items placed by all restaurants in the "  + currentBranch + " branch.", smallBold));
		preface.add(new Paragraph("* Performance report - Amount of times delivery of orders were late and on time by restaurants in the " + currentBranch + " branch.", smallBold));


		addEmptyLine(preface, 8);

		preface.add(new Paragraph("Note: This document represents the combined values for all the restaurants from a certain branch", redFont));

		document.add(preface);
		// Start a new page
		document.newPage();
	}

	private void addIncomeContent(Document document, PdfWriter writer) throws DocumentException {
		Anchor anchor = new Anchor("Income Quarter Report", catFont);
		anchor.setName("Income Quarter Report");

		// Second parameter is the number of the chapter
		Chapter catPart = new Chapter(new Paragraph(anchor), 1);

		Paragraph subPara = new Paragraph("Table Representation", subFont);

		Section subCatPart = catPart.addSection(subPara);
		subCatPart.add(new Paragraph(" "));
		subCatPart.add(
				new Paragraph("This table shows the total number of orders and revenues" + " of each restaurant in the "
						+ currentBranch + " branch on quarter ( " + currentQuarter + " ) in year (" + currentYear+")"));

		subCatPart.add(new Paragraph("Income Table:", smallBold));
		subCatPart.add(new Paragraph(" "));

		// Table representation of all the revenue and orders of the branch's restaurants
		createIncomeTable(subCatPart);
		subCatPart.add(new Paragraph(" "));
		subCatPart.add(new Paragraph("- Numbers represented in Total Income column are in NIS (New Israeli Shekel).\n"
				+ "- Numbers represented in Total Orders column are amount of orders.", smallBold));
		subCatPart.add(new Paragraph(" "));

		// Facts about the restaurants from the certain branch
		
		// Income facts - Most, least and total income
		subPara = new Paragraph("Income facts", subFont);
		subCatPart = catPart.addSection(subPara);
		subCatPart.add(new Paragraph(" "));
		subCatPart.add(new Paragraph("*   Restaurant " + mostIncomeFromRestaurantName + " is leading in most Income with " + mostIncomeFromRestaurant + " NIS."));
		subCatPart.add(new Paragraph("*   Restaurant " + leastIncomeFromRestaurantName + " has the least Income with " + leastIncomeFromRestaurant + " NIS."));
		subCatPart.add(new Paragraph("*   The total income from all the restaurants is " + totalQuarterIncome + " NIS."));

		// Orders facts - Most, least and total orders
		subPara = new Paragraph("Amount of orders facts", subFont);
		subCatPart = catPart.addSection(subPara);
		subCatPart.add(new Paragraph(" "));
		subCatPart.add(new Paragraph("*   Restaurant " + mostOrdersFromRestaurantName + " is leading in most Orders amount with " + mostOrdersFromRestaurant));
		subCatPart.add(new Paragraph("*   Restaurant " + leastOrdersFromRestaurantName + " has the least Orders amount with " + leastOrdersFromRestaurant));
		subCatPart.add(new Paragraph("*   The total orders amount from all the restaurants is " + totalQuarterOrders));
		subCatPart.add(new Paragraph(" "));
		subCatPart.add(new Paragraph(" "));

		// now add all this to the document
		document.add(catPart);
		
		// Next Page - Income and Orders Graphs
		anchor = new Anchor("Graph Documentation", catFont);
		anchor.setName("Histogram Representation");

		// Second parameter is the number of the chapter
		catPart = new Chapter(new Paragraph(anchor), 2);
		subPara = new Paragraph("Histograms of Income and Order count", subFont);
		subCatPart = catPart.addSection(subPara);
		subCatPart.add(new Paragraph(" "));
		subCatPart.add(new Paragraph("In this section we will present the histogram graphs of income and "
				+ "					amount of orders from all the restaurants in the "+ currentBranch+" branch that appeared on Quarter ( "+currentQuarter+" ) and Year ( "+currentYear+" )"));
		
		// now add all this to the document
		document.add(catPart);
		Image img = writeChartToPDF(generateIncomeBarChart(), 500, 300, writer);
		document.add(img);
		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));
		img = writeChartToPDF(generateOrdersBarChart(), 500, 300, writer);
		document.add(img);
	}
	
	private void addItemsContent(Document document, PdfWriter writer) throws DocumentException {
		
		Anchor anchor = new Anchor("Items Quarter Report", catFont);
		anchor.setName("Items Quarter Report");

		// Second parameter is the number of the chapter
		Chapter catPart = new Chapter(new Paragraph(anchor), 1);
		Paragraph subPara = new Paragraph("Pie Chart Representation", subFont);
		Section	subCatPart = catPart.addSection(subPara);
		subCatPart.add(new Paragraph(" "));
		subCatPart.add(new Paragraph("In this section we will represent a pie chart of item types that were ordered from all the restaurants in the "+ currentBranch+" branch that appeared on Quarter ( "+currentQuarter+" ) and Year ( "+currentYear+" )"));
		subCatPart.add(new Paragraph(" "));
		subCatPart.add(new Paragraph("Note: The values for each pie chart piece represents the amount of the certain item dish."));
		subCatPart.add(new Paragraph(" "));
		subCatPart.add(new Paragraph(" "));

		// now add all this to the document
		document.add(catPart);
		Image img = writeChartToPDF(generateItemPieChart(), 500, 300, writer);
		document.add(img);
		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));
	}

	private void addPerformanceContent(Document document, PdfWriter writer) throws DocumentException {
		Anchor anchor = new Anchor("Performance Quarter Report", catFont);
		anchor.setName("Performance Quarter Report");

		// Second parameter is the number of the chapter
		Chapter catPart = new Chapter(new Paragraph(anchor), 1);
		Paragraph subPara = new Paragraph("Pie Chart Representation", subFont);
		Section	subCatPart = catPart.addSection(subPara);
		subCatPart.add(new Paragraph(" "));
		subCatPart.add(new Paragraph("In this section we will present the pie chart graphs of restaurants deliveries there were delivered on time and also "
				+ "					deliveries there were late on deliver."));
		subCatPart.add(new Paragraph(" "));
		subCatPart.add(new Paragraph("The pie chart represents all the deliveries of orders from restaurants in the "+ currentBranch+" branch that appeared on Quarter ( "+currentQuarter+" ) and Year ( "+currentYear+" )"));
		subCatPart.add(new Paragraph(" "));
		subCatPart.add(new Paragraph("Note: The values for each pie chart piece represents the amount of late and on time deliveries from all the restaurants combined."));
		subCatPart.add(new Paragraph(" "));

		// now add all this to the document
		document.add(catPart);
		Image img = writeChartToPDF(generatePerformancePieChart(), 500, 300, writer);
		document.add(img);
		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));
		
	}

	private void createIncomeTable(Section subCatPart) throws BadElementException {
		PdfPTable table = new PdfPTable(3);

		PdfPCell c1 = new PdfPCell(new Phrase("Restaurant Name"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		

		c1 = new PdfPCell(new Phrase("Total Income"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Total Orders"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		table.setHeaderRows(1);

		JSONArray quarterlyReport = (JSONArray) quarterReports.get("income reports data");
		Integer reportSize = quarterlyReport.size();

		for (int i = 0; i < reportSize; i++) {
			JSONObject singleRowReport = (JSONObject) quarterlyReport.get(i);
			table.addCell(Message.getValue(singleRowReport, "restaurantName"));
			table.addCell(singleRowReport.get("totalIncome") + "");
			table.addCell(singleRowReport.get("totalOrders") + "");

			long currentRestaurantIncome = (long) singleRowReport.get("totalIncome");
			long currentRestaurantOrders = (long) singleRowReport.get("totalOrders");
			String currentRestaurantName = Message.getValue(singleRowReport, "restaurantName");

			if (currentRestaurantIncome > mostIncomeFromRestaurant) {
				mostIncomeFromRestaurant = currentRestaurantIncome;
				mostIncomeFromRestaurantName = currentRestaurantName;
			}

			if (currentRestaurantOrders > mostOrdersFromRestaurant) {
				mostOrdersFromRestaurant = currentRestaurantOrders;
				mostOrdersFromRestaurantName = currentRestaurantName;
			}

			if (leastIncomeFromRestaurant == 0) {
				leastIncomeFromRestaurant = currentRestaurantIncome;
				leastIncomeFromRestaurantName = currentRestaurantName;
			} else if (leastIncomeFromRestaurant > currentRestaurantIncome) {
				leastIncomeFromRestaurant = currentRestaurantIncome;
				leastIncomeFromRestaurantName = currentRestaurantName;
			}

			if (leastOrdersFromRestaurant == 0) {
				leastOrdersFromRestaurant = currentRestaurantOrders;
				leastOrdersFromRestaurantName = currentRestaurantName;
			} else if (leastOrdersFromRestaurant > currentRestaurantOrders) {
				leastOrdersFromRestaurant = currentRestaurantOrders;
				leastOrdersFromRestaurantName = currentRestaurantName;
			}

			totalQuarterIncome += currentRestaurantIncome;
			totalQuarterOrders += currentRestaurantOrders;

		}
		
		subCatPart.add(table);

	}

	private void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

	public JFreeChart generateItemPieChart() {
		
		JSONArray quarterlyReport = (JSONArray) quarterReports.get("items reports data");
		DefaultPieDataset dataSet = new DefaultPieDataset();

		List<String> itemList;
		for (int i = 0; i < quarterlyReport.size(); i++) {
			JSONObject restaurantValues = (JSONObject) quarterlyReport.get(i);
			long itemCount = (long) restaurantValues.get("itemCount");
			String itemType = (String) restaurantValues.get("itemType");
			itemList = (List<String>) dataSet.getKeys();
			if(!itemList.contains(itemType)) {
				dataSet.setValue(itemType, itemCount);
			} else {
				double prevColumnValue = (double) dataSet.getValue(itemType);
				dataSet.setValue(itemType, prevColumnValue + itemCount);
			}
		}
		
		JFreeChart chart = ChartFactory.createPieChart("Piechart of Item Types", dataSet, true, true, false);

        PiePlot plot = (PiePlot) chart.getPlot();
        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
        plot.setLabelGenerator(gen);
		return chart;
	}

	public JFreeChart generatePerformancePieChart() {
		
		JSONArray quarterlyReport = (JSONArray) quarterReports.get("performance reports data");
		DefaultPieDataset dataSet = new DefaultPieDataset();

		List<String> itemList;
		
		dataSet.setValue("On Time", 0);
		dataSet.setValue("Late Time", 0);

		for (int i = 0; i < quarterlyReport.size(); i++) {
			JSONObject restaurantValues = (JSONObject) quarterlyReport.get(i);
			long onTimeCount = (long) restaurantValues.get("onTimeCount");
			long lateTimeCount = (long) restaurantValues.get("lateTimeCount");
			
			double prevColumnValue = (double) dataSet.getValue("On Time");
			dataSet.setValue("On Time", prevColumnValue + onTimeCount);
			
			prevColumnValue = (double) dataSet.getValue("Late Time");
			dataSet.setValue("Late Time", prevColumnValue + lateTimeCount);
			
		}
		
		JFreeChart chart = ChartFactory.createPieChart("Piechart of performance delivery", dataSet, true, true, false);

        PiePlot plot = (PiePlot) chart.getPlot();
        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
        plot.setLabelGenerator(gen);
		return chart;
	}
	
	public JFreeChart generateIncomeBarChart() {
		
		JSONArray quarterlyReport = (JSONArray) quarterReports.get("income reports data");
		Integer maxIncomeValue = (int) mostIncomeFromRestaurant;
		Integer classWidth = maxIncomeValue / 5;
		
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		dataSet.setValue(0, "Income", "0" + " - " + classWidth);
		dataSet.setValue(0, "Income", classWidth + 1 + " - " + classWidth * 2);
		dataSet.setValue(0, "Income", classWidth * 2 + 1 + " - " + classWidth * 3);
		dataSet.setValue(0, "Income", classWidth * 3 + 1 + " - " + classWidth * 4);
		dataSet.setValue(0, "Income", classWidth * 4 + 1 + " - " + maxIncomeValue);
		
		// Creating all the histogram graph's columns
		for (int i = 0; i < quarterlyReport.size(); i++) {
			JSONObject restaurantValues = (JSONObject) quarterlyReport.get(i);
			long totalIncome = (long) restaurantValues.get("totalIncome");
			String columnName = "";
			
			if (totalIncome >= classWidth * 4 && totalIncome <= maxIncomeValue)
				columnName = classWidth * 4 + 1 + " - " + maxIncomeValue;
			else if (totalIncome >= classWidth * 3 && totalIncome <= classWidth * 4)
				columnName = classWidth * 3 + 1 + " - " + classWidth * 4;
			else if (totalIncome >= classWidth * 2 && totalIncome <= classWidth * 3)
				columnName = classWidth * 2 + 1 + " - " + classWidth * 3;
			else if (totalIncome >= classWidth * 1 && totalIncome <= classWidth * 2)
				columnName = classWidth + 1 + " - " + classWidth * 2;
			else
				columnName = "0" + " - " + classWidth;
			double prevColumnValue = (double) dataSet.getValue("Income", columnName);
			dataSet.addValue(prevColumnValue + 1, "Income", columnName);
			columnName = "";
		}
		
		JFreeChart chart = ChartFactory.createBarChart("Histogram of Income", "Income in (NIS)", "Restaurants No.",
				dataSet, PlotOrientation.VERTICAL, false, true, false);

		java.awt.Font domain = new java.awt.Font("Domain", java.awt.Font.PLAIN, 10);
		chart.getCategoryPlot().getDomainAxis().setTickLabelFont(domain);
		chart.getCategoryPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		return chart;
	}
	
	public JFreeChart generateOrdersBarChart() {
		
		JSONArray quarterlyReport = (JSONArray) quarterReports.get("income reports data");
		Integer maxOrdersValue = (int) mostOrdersFromRestaurant;
		Integer classWidth = maxOrdersValue / 5;
		
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		dataSet.setValue(0, "Orders", "0" + " - " + classWidth);
		dataSet.setValue(0, "Orders", classWidth + 1 + " - " + classWidth * 2);
		dataSet.setValue(0, "Orders", classWidth * 2 + 1 + " - " + classWidth * 3);
		dataSet.setValue(0, "Orders", classWidth * 3 + 1 + " - " + classWidth * 4);
		dataSet.setValue(0, "Orders", classWidth * 4 + 1 + " - " + maxOrdersValue);
		
		// Creating all the histogram graph's columns
		for (int i = 0; i < quarterlyReport.size(); i++) {
			JSONObject restaurantValues = (JSONObject) quarterlyReport.get(i);
			long totalIncome = (long) restaurantValues.get("totalOrders");
			String columnName = "";
			
			if (totalIncome >= classWidth * 4 && totalIncome <= maxOrdersValue)
				columnName = classWidth * 4 + 1 + " - " + maxOrdersValue;
			else if (totalIncome >= classWidth * 3 && totalIncome <= classWidth * 4)
				columnName = classWidth * 3 + 1 + " - " + classWidth * 4;
			else if (totalIncome >= classWidth * 2 && totalIncome <= classWidth * 3)
				columnName = classWidth * 2 + 1 + " - " + classWidth * 3;
			else if (totalIncome >= classWidth * 1 && totalIncome <= classWidth * 2)
				columnName = classWidth + 1 + " - " + classWidth * 2;
			else
				columnName = "0" + " - " + classWidth;
			double prevColumnValue = (double) dataSet.getValue("Orders", columnName);
			dataSet.addValue(prevColumnValue + 1, "Orders", columnName);
			columnName = "";
		}

		JFreeChart chart = ChartFactory.createBarChart("Histogram of Orders Amount", "Amount of Orders", "Restaurants No.",
				dataSet, PlotOrientation.VERTICAL, false, true, false);
		chart.getCategoryPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		return chart;
	}

}