package com.Ben12345rocks.VotingPlugin.Report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.Ben12345rocks.VotingPlugin.Main;
import com.Ben12345rocks.VotingPlugin.Config.Config;
import com.Ben12345rocks.VotingPlugin.Files.Files;

public class Report {

	static Report instance = new Report();

	static Main plugin = Main.plugin;

	public static Report getInstance() {
		return instance;
	}

	FileConfiguration data;

	File dFile;

	private Report() {
	}

	public Report(Main plugin) {
		Report.plugin = plugin;
	}

	public FileConfiguration getData() {
		return data;
	}

	public void reloadData() {
		data = YamlConfiguration.loadConfiguration(dFile);
	}

	public void saveData() {
		Files.getInstance().editFile(dFile, data);
	}

	public void create() {
		File directoryToZip = plugin.getDataFolder();

		List<File> fileList = new ArrayList<File>();
		try {
			plugin.getLogger().info(
					"---Getting references to all files in: "
							+ directoryToZip.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		getAllFiles(directoryToZip, fileList);
		plugin.getLogger().info("---Creating zip file");
		writeZipFile(directoryToZip, fileList);
		plugin.getLogger().info("---Done");
	}

	public void getAllFiles(File dir, List<File> fileList) {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				fileList.add(file);
				if (file.isDirectory()) {
					if (Config.getInstance().getDebugEnabled()) {
						plugin.getLogger().info(
								"directory:" + file.getCanonicalPath());
					}
					getAllFiles(file, fileList);
				} else {

					if (Config.getInstance().getDebugEnabled()) {
						plugin.getLogger().info(
								"file:" + file.getCanonicalPath());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeZipFile(File directoryToZip, List<File> fileList) {

		try {
			Date date = new Date();
			@SuppressWarnings("deprecation")
			FileOutputStream fos = new FileOutputStream(plugin.getDataFolder()
					.getAbsolutePath()
					+ File.separator
					+ "Report"
					+ (date.getYear() + 1900)
					+ "."
					+ (date.getMonth() + 1)
					+ "."
					+ date.getDate()
					+ "."
					+ date.getHours()
					+ "."
					+ date.getMinutes() + "." + date.getSeconds() + ".zip");
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (File file : fileList) {
				if (!file.isDirectory()) { // we only zip files, not directories
					addToZip(directoryToZip, file, zos);
				}
			}

			zos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addToZip(File directoryToZip, File file,
			ZipOutputStream zos) throws FileNotFoundException, IOException {

		FileInputStream fis = new FileInputStream(file);

		String zipFilePath = file.getPath();
		if (Config.getInstance().getDebugEnabled()) {
			plugin.getLogger()
					.info("Writing '" + zipFilePath + "' to zip file");
		}
		ZipEntry zipEntry = new ZipEntry(zipFilePath);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}
}