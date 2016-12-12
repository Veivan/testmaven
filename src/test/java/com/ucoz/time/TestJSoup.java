package com.ucoz.time;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestJSoup {

	public static void main(String[] args) throws IOException {
		Path path = Paths.get("d:\\demo.txt");
		List<String> list = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
		
		final String html = String.join("", list);
		
		Utils.readCallbackUrl(html);

	}

}
