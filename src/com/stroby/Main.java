package com.stroby;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws IOException {

		Utility.print("Startup... \r");

    	Random random = new Random();

		Utility.print("Startup... Done! \n");

		ArrayList<Statement> statements = new ArrayList<>();
		JSONArray jsonArray = (JSONArray) Utility.loadJson("/res/statement.json").get("statements");
		for (int i = 0; i < jsonArray.size(); i++) {

			Utility.print("Loading Statements... "+ i + 1 +" of "+ jsonArray.size() +" \r"); // Will never be seen.

			JSONObject jsonObject = (JSONObject) jsonArray.get(i);

			Statement statement = new Statement();
			statement.statement = (String) jsonObject.get("statement");

			JSONArray jLines = (JSONArray) jsonObject.get("lines");
			statement.lines = new String[jLines.size()];
			for (int j = 0; j < jLines.size(); j++) {
				statement.lines[j] = (String) jLines.get(j);
			}

			statements.add(statement);
		}
		Utility.print("Loading Statements... Done!\n");

	    Utility.println("Input Path of projrct dir:");
	    String projectPath = Utility.getInput();

		Utility.print("Loading Files... \r");

		ArrayList<File> files = new ArrayList<>();
		Utility.getFileFromDir(projectPath, files, ".java");

		Utility.print("Loading Files... Done!\n");

		Utility.println(files.size() +" files with .java ending found.");

		if(files.size() == 0) {
			Utility.println("No files to comment found ending program.");
			return;
		}

		for (File file : files) {

			Utility.print("Commenting files... "+ file.getName() +" at: "+ file.getAbsolutePath() +" \r");

			List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);


			for (int i = 0; i < lines.size(); i++) {

				String line = lines.get(i);

				if (line.contains("//")) continue;

				ArrayList<Statement> activeStatements = new ArrayList<>();

				for (Statement statement : statements) {
					if (line.contains(statement.statement))
						activeStatements.add(statement);
				}

				Statement usedStatement;
				if(activeStatements.isEmpty()){
					continue;
				}
				else if(activeStatements.size() > 1){
					int index = random.nextInt(activeStatements.size());
					usedStatement = activeStatements.get(index);
				}
				else{
					usedStatement = activeStatements.get(0);
				}

				String comment;
				if(usedStatement.lines.length > 1){
					int index = random.nextInt(usedStatement.lines.length);
					comment = usedStatement.lines[index];
				}
				else {
					comment = usedStatement.lines[0];
				}

				line = line +" // "+ comment;

				lines.set(i, line);
			}

			Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
		}

		Utility.print("Commenting files... Done! \n");
		Utility.println("Ending programm. \n");
    }
}

class Statement {
	public String statement;
	public String[] lines;
}
