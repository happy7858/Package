package com.shenma.common.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VideoMp4 {
	public int m_nDuration;// µ•Œª√Î
	public int m_nWidth;
	public int m_nHeight;
	public int m_nSize;
	private int m_nScaletime;
	private short m_nVolume;

	private class Box {
		byte[] data;
		String name;

		public String toString() {
			return name + "[" + data.length + "]";
		}
	}

	public boolean load(String path) {
		if (path.endsWith(".mp4") == false) {
			Log.err("invalid mp4 file path:" + path);
			return false;
		}

		try {
			FileInputStream fis = new FileInputStream(new File(path));
			DataInputStream dis = new DataInputStream(fis);
			m_nSize = dis.available();
			List<Box> boxes = loadBoxs(dis);
			for (Box box : boxes) {
				if (box.name.equals("moov")) {
					loadMoov(box);
				}
			}
			dis.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.out("load video:" + path + " " + toString());
		return true;
	}

	private List<Box> loadBoxs(byte[] data) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		List<Box> boxes = loadBoxs(dis);
		dis.close();
		bais.close();
		return boxes;
	}

	private List<Box> loadBoxs(DataInputStream dis) {
		List<Box> boxes = new ArrayList<Box>();
		try {
			int len = 0;
			while ((len = dis.readInt()) != -1) {
				if (len < 8) {
					break;
				}
				Box box = new Box();
				byte[] dataName = new byte[4];
				dis.read(dataName);
				box.name = new String(dataName);
				box.data = new byte[len - 8];
				dis.read(box.data);
				boxes.add(box);
			}
		} catch (EOFException e) {

		} catch (IOException e) {
			e.printStackTrace();
		}

		return boxes;
	}

	private void loadMoov(Box box) throws IOException {
		List<Box> boxes = loadBoxs(box.data);
		for (Box box2 : boxes) {
			if (box2.name.equals("mvhd")) {
				loadMvhd(box2.data);
			} else if (box2.name.equals("trak")) {
				loadTrak(box2.data);
			}

		}
	}

	private void loadTrak(byte[] data) throws IOException {
		List<Box> boxes = loadBoxs(data);
		for (Box box : boxes) {
			if (box.name.equals("mdia")) {
				loadMdia(box.data);
			} else if (box.name.equals("tkhd")) {
				loadTkhd(box.data);
			}

		}
	}

	private void loadMdia(byte[] data) throws IOException {
		List<Box> boxes = loadBoxs(data);
		for (Box box : boxes) {
			if (box.name.equals("minf")) {
				loadMinf(box.data);
			}
		}
	}

	private void loadMinf(byte[] data) throws IOException {
		List<Box> boxes = loadBoxs(data);
		for (Box box : boxes) {
			if (box.name.equals("stbl")) {
				loadStbl(box.data);
			}
		}
	}

	private void loadStbl(byte[] data) throws IOException {
		List<Box> boxes = loadBoxs(data);
		for (Box box : boxes) {
			if (box.name.equals("stsd")) {
				loadStsd(box.data);
			}
		}
	}

	private void loadStsd(byte[] data) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		dis.skip(8);
		List<Box> boxes = loadBoxs(dis);
		for (Box box : boxes) {
			if (box.name.equals("avc1")) {
				loadAvc1(box.data);
			}
		}

		dis.close();
		bais.close();
	}

	private void loadAvc1(byte[] data) throws IOException {
		ByteArrayInputStream baos = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
		dis.skip(24);
		m_nWidth = dis.readShort();
		m_nHeight = dis.readShort();
		dis.close();
		baos.close();

	}

	private void loadTkhd(byte[] data) throws IOException {
		ByteArrayInputStream baos = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

		int vesion = dis.read();
		dis.skip(3);// flag

		int createTime = dis.readInt();
		int modifyTime = dis.readInt();
		int Scaletime = dis.readInt();
		int nDuration = dis.readInt() / Scaletime;
		int speed = dis.readInt();
		int nVolume = dis.readShort();

		dis.close();
		baos.close();
	}

	private void loadMvhd(byte[] data) throws IOException {
		ByteArrayInputStream baos = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

		int vesion = dis.read();
		dis.skip(3);// flag

		int createTime = dis.readInt();
		int modifyTime = dis.readInt();
		m_nScaletime = dis.readInt();
		m_nDuration = dis.readInt() / m_nScaletime;
		int speed = dis.readInt();
		m_nVolume = dis.readShort();

		dis.close();
		baos.close();
	}

	@Override
	public String toString() {
		return String.format("[%d-%d]%ds", m_nWidth, m_nHeight, m_nDuration);
	}

}
