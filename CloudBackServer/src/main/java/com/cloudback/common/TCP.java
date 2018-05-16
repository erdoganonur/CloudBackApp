package com.cloudback.common;

/*********************************************************************************
Author          : Halic Project Group
Last Update     : 28.10.2012
Platform        : Java VM and Linux

TCP class that simplifies the most used operations in client/server programming
paradigm using TCP protocol
**********************************************************************************/
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
//IMPORTANT: There is/are TODO(s) in the code 


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCP {	
	//SocketType enum Declaration
	public enum SocketType {
		SERVER(1), CLIENT(2);
		SocketType(int val)
		{
			m_socketType = val;			
		}
		public int GetSocketType()
		{
			return m_socketType;			
		}
		private final int m_socketType;
	}
	
	//TCPException class (Checked)
	public static final class TCPException extends Exception {		
		public TCPException(String msg, Exception innerException)
		{
			super(msg);			
			m_innerException = innerException;
		}
		public TCPException(String msg)
		{
			this(msg, null);
		}
		public Exception GetInnerException()
		{
			return m_innerException;
		}		
		private Exception m_innerException;
		private static final long serialVersionUID = 1L;
	}
	
	//public constructors
	public TCP(int portNo) throws TCPException
	{	
		if (portNo < 0)
			throw new TCPException("WRONG PORT NUMBER");
		
		try {			
			m_sockType = SocketType.SERVER;
			m_portNo = portNo;
			m_serverSocket = new ServerSocket(portNo);
		}
		catch (IOException ex) {
			throw new TCPException(ex.getMessage(), ex);
		}
		catch (SecurityException ex) {
			throw new TCPException(ex.getMessage(), ex);			
		}
	}
	
	public TCP(SocketType sockType, String ipAddr, int portNo) throws TCPException
	{
		if (portNo < 0)
			throw new TCPException("WRONG PORT NUMBER");
		
		if (sockType != SocketType.SERVER && sockType != SocketType.CLIENT)
			throw new TCPException("WRONG SOCKET TYPE");
		
		try {
			m_sockType = sockType;
			m_ipAddr = ipAddr;
			m_portNo = portNo;
			if (m_sockType == SocketType.SERVER)
				m_serverSocket = new ServerSocket(portNo);
		}
		catch (IOException ex) {
			throw new TCPException(ex.getMessage(), ex);
		}
		catch (SecurityException ex) {
			throw new TCPException(ex.getMessage(), ex);			
		}
	}
	
	/*
	public TCP(int sockType, int portNo, int backLog) // will be modified
	{
		m_serverSocket = new ServerSocket(portNo, backLog)
	}
	
	public TCP(int sockType, int portNo, int backLog, InetAddress bindAddr) // will be modified
	{
		m_serverSocket = new ServerSocket(portNo, backLog, bindAddr)
	}
	
	public TCP(DataOutputStream dos) // will be modified
	{
		m_dos = dos;
		
	}
	
	public TCP(DataInputStream dis) // will be modified
	{
		m_dis = dis;		
	}
	
	public TCP(DataOutputStream dos, DataInputStream dis) // will be modified
	{
		m_dos = dos;
		m_dis = dis;		
	}
	*/
	
	//private constructors
	protected TCP(Socket socket, boolean bInputStream, boolean bOutputStream) throws TCPException
	{
		try {
			m_socket = socket;
			m_socket.setSoTimeout(0);
			
			if (bInputStream)
				m_dis = new DataInputStream(m_socket.getInputStream());
			
			if (bOutputStream)
				m_dos = new DataOutputStream(m_socket.getOutputStream());
		}
		catch (IOException ex) {
			throw new TCPException(ex.getMessage(), ex);
		}
	}
	
	protected TCP(Socket socket) throws TCPException
	{
		try {
			m_socket = socket;			
			m_dis = new DataInputStream(m_socket.getInputStream());
			m_dos = new DataOutputStream(m_socket.getOutputStream());
		}
		catch (IOException ex) {
			throw new TCPException(ex.getMessage(), ex);
		}
	}
	
	//Set Methods
	public void SetServerSocket(ServerSocket serverSocket) throws TCPException
	{
		if (m_socket == null)
			throw new TCPException("Object references client socket"); 
			
		try {
			if (serverSocket == null)
				throw new NullPointerException("ServerSocket is not allocated");
		}
		catch (NullPointerException ex) {
			throw new TCPException(ex.getMessage(), ex);
		}
		
		m_serverSocket = serverSocket;		
	}
	
	public void SetSocket(Socket socket) throws TCPException
	{
		if (m_serverSocket == null)
			throw new TCPException("Object references server socket"); 
		
		try {
			if (socket == null)
				throw new NullPointerException("ClientSocket is not allocated");
		}
		catch (NullPointerException ex) {
			throw new TCPException(ex.getMessage(), ex);
		}		
		m_socket = socket;		
	}
	
	public void SetDataOutputStream(DataOutputStream dos) throws TCPException
	{
		try {
			if (dos == null)
				throw new NullPointerException("DataOutputStream not allocated");
		}
		catch (NullPointerException ex) {
			throw new TCPException(ex.getMessage(), ex);
		}
		
		m_dos = dos;
	}
	
	public void SetDataInputStream(DataInputStream dis) throws TCPException
	{
		try {
			if (dis == null)
				throw new NullPointerException("DataInputStream not allocated");
		}
		catch (NullPointerException ex) {
			throw new TCPException(ex.getMessage(), ex);
		}
		
		m_dis = dis;
	}	
	
	//GetMethods
	public SocketType GetSockType()
	{
		return m_sockType;
	}
	
	public ServerSocket GetServerSocket()
	{	
		return m_serverSocket;		
	}
	
	public Socket GetSocket()
	{	
		return m_socket;		
	}
	
	public DataOutputStream GetDataOutputStream()
	{
		return m_dos;
	}
	
	public DataInputStream GetDataInputStream()
	{
		return m_dis;
	}
	
	public InetAddress GetInetAddress()
	{
		return m_socket.getInetAddress();
	}
	
	public String GetHostAddress()
	{
		return GetInetAddress().getHostAddress();
	}
		
	//public network functions
	public TCP Accept() throws TCPException
	{
		try {
			return new TCP(m_serverSocket.accept());
		}
		catch (IOException ex) {
			throw new TCPException(ex.getMessage(), ex);		
		}
	}
	
	public TCP Accept(boolean bInputStream, boolean bOutputStream)  throws TCPException 
	{
		try {
			return new TCP(m_serverSocket.accept(), bInputStream, bOutputStream);
		}
		catch (IOException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}
		
	}
	
	public void Connect(String host, int portNo, boolean bInputStream, boolean bOutputStream)  throws TCPException
	{
		if (portNo < 0)
			throw new TCPException("WRONG PORT NUMBER");
		
		try {
			m_socket = new Socket(host, portNo);
			
			if (bInputStream)
				m_dis = new DataInputStream(m_socket.getInputStream());
			
			if (bOutputStream)
				m_dos = new DataOutputStream(m_socket.getOutputStream());
		}
		catch (IOException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}
		catch (SecurityException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}	
	}
	
	public void Connect(String host, int portNo)  throws TCPException
	{
		if (portNo < 0)
			throw new TCPException("WRONG PORT NUMBER");
		
		try {
			m_socket = new Socket(host, portNo);			
			m_dos = new DataOutputStream(m_socket.getOutputStream());		
			m_dis = new DataInputStream(m_socket.getInputStream());
		}
		catch (IOException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}
		catch (SecurityException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}	
	}
	
	public void Connect(boolean bInputStream, boolean bOutputStream)  throws TCPException
	{
		if (m_portNo < 0)
			throw new TCPException("Port is not specified");
		
		try { 
			if (m_ipAddr == null)
				throw new NullPointerException("IP address is not specified");	
				
			m_socket = new Socket(m_ipAddr, m_portNo);
			
			if (bInputStream)
				m_dis = new DataInputStream(m_socket.getInputStream());
			
			if (bOutputStream)
				m_dos = new DataOutputStream(m_socket.getOutputStream());			
		}
		catch (NullPointerException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}
		catch (IOException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}
		catch (SecurityException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}	
	}
	
	public void Connect() throws TCPException
	{
		if (m_portNo < 0)
			throw new TCPException("Port is not specified");
		
		try {
			if (m_ipAddr == null)
				throw new NullPointerException("IP address is not specified");	
			
			m_socket = new Socket(m_ipAddr, m_portNo);		
			m_dos = new DataOutputStream(m_socket.getOutputStream());		
			m_dis = new DataInputStream(m_socket.getInputStream());
		}
		catch (NullPointerException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}	
		catch (IOException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}
		catch (SecurityException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}	
	}
	
	public void CloseSafely(boolean bshutDownInput, boolean bshutDownOutput) throws TCPException
	{
		try {
			if (m_socket == null)
				throw new NullPointerException("Not a Socket");
			
			if (bshutDownOutput)
				m_socket.shutdownOutput();
			
			if (bshutDownInput)
				m_socket.shutdownInput();
			
			if (m_dos != null)
				m_dos.close();
			
			if (m_dis != null)
				m_dis.close();
			
			if (!m_socket.isClosed())
				m_socket.close();	
		}
		catch (NullPointerException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}
		catch (IOException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}		
	}
	
	public void CloseServer() throws TCPException	
	{
		try {
			if (m_serverSocket == null)
				throw new NullPointerException("Not a ServerSocket");
			
			if (!m_serverSocket.isClosed())
				m_serverSocket.close();			
		}
		catch (NullPointerException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}
		catch (IOException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}		
	}	
	
	//Send methods for primitive types
	public void SendShort(short val) throws TCPException
	{
		byte [] data = null;		
		
		data = short2ByteA(val);		
		SendFixedData(data, 0, data.length);
		
	}
	
	public void SendInt(int val) throws TCPException
	{
		byte [] data = null;
				
		data = int2ByteA(val);		
		SendFixedData(data, 0, data.length);		
	}	
	
	public void SendLong(long val) throws TCPException
	{
		byte [] data = null;
		
	
		data = long2ByteA(val);		
		SendFixedData(data, 0, data.length);	
	}
	
	public void SendFloat(float val) throws TCPException
	{
		byte [] data = null;
				
		data = float2ByteA(val);		
		SendFixedData(data, 0, data.length);		
	}
	
	public void SendDouble(double val) throws TCPException
	{
		byte [] data = null;
					
		data = double2ByteA(val);		
		SendFixedData(data, 0, data.length);		
	}
	
	public void SendByte(byte val) throws TCPException
	{
		byte [] data = null;
				
		data = byte2ByteA(val);		
		SendFixedData(data, 0, data.length);
	}
	
	public void SendChar(char val) throws TCPException
	{
		byte [] data = null;		
			
		data = char2ByteA(val);		
		SendFixedData(data, 0, data.length);		
	}
	
	public void SendBoolean(boolean val) throws TCPException
	{
		byte [] data = null;
		
				
		data = boolean2ByteA(val);		
		SendFixedData(data, 0, data.length);		
	}	
	
	public void SendString(String str) throws TCPException
	{
		byte [] data = null;
				
		data = string2ByteA(str);		
		SendInt(data.length);		
		SendFixedData(data, 0, data.length);		
	}
	
	//Receive methods for primitive types
	public short ReceiveShort() throws TCPException
	{		
		byte [] data = new byte[2];
		
		ReceiveFixedData(data, 0, data.length);
			
		return byteA2Short(data);				
	}
	
	public int ReceiveInt() throws TCPException
	{		
		byte [] data = new byte[4];
		
		ReceiveFixedData(data, 0, data.length);
			
		return byteA2Int(data);	
	}	
	
	public long ReceiveLong() throws TCPException
	{	
		byte [] data = new byte[8];
		
		ReceiveFixedData(data, 0, data.length);
		
		return byteA2Long(data);		
	}
	
	public float ReceiveFloat() throws TCPException
	{
		byte [] data = new byte[4];
		
		ReceiveFixedData(data, 0, data.length);
		
		return byteA2Float(data);		
	}
	
	public double ReceiveDouble() throws TCPException
	{		
		byte [] data = new byte[8];
		
		ReceiveFixedData(data, 0, data.length);
			
		return byteA2Double(data);		
	}
	
	public byte ReceiveByte() throws TCPException
	{
		byte [] data = new byte[1];
		
		ReceiveFixedData(data, 0, data.length);
		
		return byteA2Byte(data);		
	}
	
	public char ReceiveChar() throws TCPException
	{
		byte [] data = new byte[2];
		
		ReceiveFixedData(data, 0, data.length);
		
		return byteA2Char(data);		
	}
	
	public boolean ReceiveBoolean() throws TCPException
	{	
		byte [] data = new byte[1];		
		ReceiveFixedData(data, 0, data.length);
		
		return byteA2Boolean(data);	
	}	
	
	public String ReceiveString() throws TCPException
	{		
		int dataLen = ReceiveInt();		
		byte [] data = new byte[dataLen];
			
		ReceiveFixedData(data, 0, data.length);		
			
		return byteA2String(data);		
	}
	
	//Send methods for primitive typed arrays
	public void SendShortA(short [] array) throws TCPException
	{		
		SendInt(array.length);
			
		for (int i = 0; i < array.length; ++i)
			SendShort(array[i]);		
	}
	
	public void SendIntA(int [] array) throws TCPException
	{	
		SendInt(array.length);
			
		for (int i = 0; i < array.length; ++i)
			SendInt(array[i]);	
	}
	
	public void SendLongA(long [] array) throws TCPException
	{	
		SendInt(array.length);
		
		for (int i = 0; i < array.length; ++i)
			SendLong(array[i]);
	}
	
	public void SendFloatA(float [] array) throws TCPException
	{	
		SendInt(array.length);
		
		for (int i = 0; i < array.length; ++i)
			SendFloat(array[i]);
	}
	
	public void SendDoubleA(double [] array) throws TCPException
	{	
		SendInt(array.length);
		
		for (int i = 0; i < array.length; ++i)
			SendDouble(array[i]);
	}
	
	public void SendByteA(byte [] array) throws TCPException
	{	
		SendVarData(array);
	}
	
	public void SendCharA(char [] array) throws TCPException
	{	
		SendInt(array.length);
		
		for (int i = 0; i < array.length; ++i)
			SendChar(array[i]);
	}
	
	public void SendBooleanA(boolean [] array) throws TCPException
	{	
		SendInt(array.length);
		
		for (int i = 0; i < array.length; ++i)
			SendBoolean(array[i]);
	}
	
	public void SendStringA(String [] array) throws TCPException
	{	
		SendInt(array.length);
		
		for (int i = 0; i < array.length; ++i)
			SendString(array[i]);
	}
	
	//Receive methods for primitive typed arrays
	public short [] ReceiveShortA() throws TCPException
	{
		int len = ReceiveInt();
		short [] result = new short[len];		
		
		for (int i = 0; i < len; ++i)
			result[i] = ReceiveShort();
		
		return result;			
	}
	
	public int [] ReceiveIntA() throws TCPException
	{
		int len = ReceiveInt();
		int [] result = new int[len];		
		
		for (int i = 0; i < len; ++i)
			result[i] = ReceiveInt();
		
		return result;
	}
	
	public long [] ReceiveLongA() throws TCPException
	{
		int len = ReceiveInt();
		long [] result = new long[len];		
		
		for (int i = 0; i < len; ++i)
			result[i] = ReceiveLong();
		
		return result;
	}
	
	public float [] ReceiveFloatA() throws TCPException
	{
		int len = ReceiveInt();
		float [] result = new float[len];		
		
		for (int i = 0; i < len; ++i)
			result[i] = ReceiveFloat();
		
		return result;
	}
	
	public double [] ReceiveDoubleA() throws TCPException
	{
		int len = ReceiveInt();
		double [] result = new double[len];		
		
		for (int i = 0; i < len; ++i)
			result[i] = ReceiveDouble();
		
		return result;
	}
	
	public byte [] ReceiveByteA() throws TCPException
	{	
		return ReceiveVarData();
	}
	
	public char [] ReceiveCharA() throws TCPException
	{
		int len = ReceiveInt();
		char [] result = new char[len];		
		
		for (int i = 0; i < len; ++i)
			result[i] = ReceiveChar();
		
		return result;
	}
	
	public boolean [] ReceiveBooleanA() throws TCPException
	{
		int len = ReceiveInt();
		boolean [] result = new boolean[len];		
		
		for (int i = 0; i < len; ++i)
			result[i] = ReceiveBoolean();
		
		return result;
	}
	
	public String [] ReceiveStringA() throws TCPException
	{
		int len = ReceiveInt();
		String [] result = new String[len];		
		
		for (int i = 0; i < len; ++i)
			result[i] = ReceiveString();
		
		return result;
	}
	
	//File send/receive operations
	public void SendFile(String fileName, int chunkSize) throws TCPException
	{
		RandomAccessFile file = null;
		long fileLen = 0, nChunk = 0;
		int nRead = 0;
		byte [] data = null;	
		
		try {
			file = new RandomAccessFile(fileName, "r");
			fileLen = file.length();
			
			SendLong(fileLen);
			
			nChunk = fileLen / chunkSize;
			
			if (fileLen % chunkSize != 0)			
				nChunk++;	
			
			data = new byte[chunkSize];				
			
			for (long i = 0; i < nChunk; ++i) {
				nRead = file.read(data, 0, chunkSize);
				SendFixedData(data, 0, nRead);				
			}
		}
		catch (FileNotFoundException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}
		catch (IOException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}
		finally {	
			try {
				file.close();
			}
			catch (IOException ex) {
				throw new TCPException(ex.getMessage(), ex);	
			}
		}
	}	
	
	public void ReceiveFile(String fileName, int chunkSize) throws TCPException
	{
		RandomAccessFile file = null;
		long fileLen = 0, nChunk = 0, nLoop;
		int remBytes = 0;
		byte [] data = null;		
		
		try {
			file = new RandomAccessFile(fileName, "rw");
			fileLen = ReceiveLong();
			
			nChunk = fileLen / chunkSize;
			
			if (fileLen % chunkSize != 0) {
				remBytes = (int)(fileLen % chunkSize);
				nChunk++;
			}
			
			data = new byte[chunkSize];
			
			nLoop = remBytes != 0 ? nChunk - 1 : nChunk;
			
			for (long i = 0; i < nLoop; ++i) {
				ReceiveFixedData(data, 0, chunkSize);
				file.write(data, 0, chunkSize);					
			}				
			
			if (remBytes != 0) {
				data = new byte[remBytes];
				ReceiveFixedData(data, 0, remBytes);
				file.write(data, 0, remBytes);
			}
		}		
		catch (IOException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}
		finally {	
			try {
				file.close();
			}
			catch (IOException ex) {
				throw new TCPException(ex.getMessage(), ex);	
			}
		}		
	}
	
	//Send function for variable data
	public void SendVarData(byte [] data) throws TCPException
	{		
		SendInt(data.length);
		SendFixedData(data, 0, data.length);					
	}
		
	//Receive functions for variable data		
	public byte [] ReceiveVarData() throws TCPException
	{			
		return ReceiveFixedData(ReceiveInt());
	}
	
	//Send function for fixed data
	public void SendFixedData(byte [] data, int off, int len) throws TCPException
	{
		int curOffset = off;
		int left = len;
		int written = 0;
		int prevSize = m_dos.size();
		
		try {
			while (curOffset < len) {
				m_dos.write(data, curOffset, left);
				m_dos.flush();
				written = m_dos.size() - prevSize;
				prevSize = m_dos.size();
				left -= written;
				curOffset += written;			
			}	
		}
		catch (IOException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}	
	}
	
	//Receive functions for fixed data	
	public void ReceiveFixedData(byte [] data, int off, int len) throws TCPException
	{
		int curOffset = off;
		int left = len;
		int ret = 0;
		
		try {
			while (curOffset < len) {
				ret = m_dis.read(data, curOffset, left);
				//if (ret == -1)
				//	return;
				
				left -= ret;
				curOffset += ret;
			}	
		}
		catch (IOException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}	
	}
	
	//TODO: Think about and find the best solution for the strange behavior
	public byte [] ReceiveFixedData(int len) throws TCPException
	{
		int curOffset = 0;
		int left = len;
		int ret = 0;		
		byte [] resultData = new byte[len];
		
		try {		
			while (curOffset < len) {
				ret = m_dis.read(resultData, curOffset, left);
				//if (ret == -1)
					//throw new IOException("Strange Behavior");
				
				left -= ret;
				curOffset += ret;
			}		
			
			return resultData;
		}
		catch (IOException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}	
	}
	
	//Destructor	
	protected void finalize()
	{
		m_sockType = null;
		m_serverSocket = null;
		m_socket = null;
		m_dos = null;
		m_dis = null;		
	}
	
	//protected utility function there is a bug in this method
	protected boolean isLittleEndian()
	{
		short val = 1;
		byte b = (byte) val;
		
		return b == 1;
	}
	
	//Utility methods
	protected byte[] short2ByteA(short data)
	{
		if (isLittleEndian())
			return new byte[] {(byte)((data >> 8) & 0xff), (byte)((data >> 0) & 0xff)};
		
		return new byte[] {(byte)((data >> 0) & 0xff), (byte)((data >> 8) & 0xff)};
	}	
	
	protected short byteA2Short(byte[] data) throws TCPException 
	{	
		try {
			if (data == null)
				throw new NullPointerException("Array is not allocated");
			
			if (data.length != 2)
				throw new NumberFormatException("Array size is not agreed with short type");
		
			if (isLittleEndian())
				return (short)((0xff & data[0]) << 8 | (0xff & data[1]) << 0);
			
			return (short)((0xff & data[1]) << 8 | (0xff & data[0]) << 0);			
		}
		catch (NullPointerException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}	
		catch (NumberFormatException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}	
	}
	
	protected byte[] int2ByteA(int data)
	{
		return new byte[] {(byte)((data >> 24) & 0xff), (byte)((data >> 16) & 0xff), (byte)((data >> 8) & 0xff), (byte)((data >> 0) & 0xff)};
	}
	
	protected int byteA2Int(byte[] data) throws TCPException
	{
		try {
			if (data == null)
				throw new NullPointerException("Array is not allocated");
			
			if (data.length != 4)
				throw new NumberFormatException("Array size is not agreed with int type");		
			
			return (int)((0xff & data[0]) << 24 | (0xff & data[1]) << 16 | (0xff & data[2]) << 8 | (0xff & data[3]) << 0);
		}
		catch (NullPointerException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}	
		catch (NumberFormatException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}	
	}
	
	protected byte[] long2ByteA(long data) 
	{
		return new byte[] { (byte)((data >> 56) & 0xff), (byte)((data >> 48) & 0xff), (byte)((data >> 40) & 0xff), 
							(byte)((data >> 32) & 0xff), (byte)((data >> 24) & 0xff), (byte)((data >> 16) & 0xff),
							(byte)((data >> 8) & 0xff), (byte)((data >> 0) & 0xff) };
	}
		
	protected long byteA2Long(byte[] data) throws TCPException
	{
		try {
			if (data == null)
				throw new NullPointerException("Array is not allocated");
			
			if (data.length != 8)
				throw new NumberFormatException("Array size is not agreed with long type");			
		
			return (long)( 	(long)(0xff & data[0]) << 56 | (long)(0xff & data[1]) << 48 | (long)(0xff & data[2]) << 40 |
							(long)(0xff & data[3]) << 32 | (long)(0xff & data[4]) << 24 | (long)(0xff & data[5]) << 16 |
							(long)(0xff & data[6]) << 8 | (long)(0xff & data[7]) << 0  );
		}
		catch (NullPointerException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}	
		catch (NumberFormatException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}	
	}	
	
	protected byte[] float2ByteA(float data) 
	{	
		return int2ByteA(Float.floatToRawIntBits(data));
	}	
	
	protected float byteA2Float(byte[] data) throws TCPException
	{	
		try {
			if (data == null)
				throw new NullPointerException("Array is not allocated");
			
			if (data.length != 4)
				throw new NumberFormatException("Array size is not agreed with float type");		
				
			return Float.intBitsToFloat(byteA2Int(data));
		}
		catch (NullPointerException ex) {
			throw new TCP.TCPException(ex.getMessage(), ex);	
		}	
		catch (NumberFormatException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}	
	}	
	
	protected byte[] double2ByteA(double data) 
	{	
		return long2ByteA(Double.doubleToRawLongBits(data));
	}
		
	protected double byteA2Double(byte[] data) throws TCPException
	{
		try {
			if (data == null)
				throw new NullPointerException("Array is not allocated");
			
			if (data.length != 8)
				throw new NumberFormatException("Array size is not agreed with double type");		
		
			return Double.longBitsToDouble(byteA2Long(data));
		}
		catch (NullPointerException ex) {
			throw new TCP.TCPException(ex.getMessage(), ex);	
		}	
		catch (NumberFormatException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}	
	}	
	
	protected byte[] byte2ByteA(byte data) 
	{
		return new byte[]{data};	
	}
	
	protected byte byteA2Byte(byte[] data) 
	{
		return (data == null || data.length == 0) ? 0x0 : data[0];	
	}
	
	protected byte[] char2ByteA(char data)
	{
		return new byte[] {(byte)((data >> 8) & 0xff), (byte)((data >> 0) & 0xff)};	
	}
	
	protected char byteA2Char(byte[] data) throws TCPException
	{
		try {
			if (data == null)
				throw new NullPointerException("Array is not allocated");
			
			if (data.length != 2)
				throw new NumberFormatException("Array size is not agreed with char type");		
		
			return (char)((0xff & data[0]) << 8 | (0xff & data[1]) << 0);
		}
		catch (NullPointerException ex) {
			throw new TCP.TCPException(ex.getMessage(), ex);	
		}	
		catch (NumberFormatException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}	
	}
	
	protected byte[] boolean2ByteA(boolean data) 
	{
		return new byte[]{(byte)(data ? 0x01 : 0x00)};	
	}
		
	protected boolean byteA2Boolean(byte[] data) throws TCPException
	{
		try {
			if (data == null)
				throw new NullPointerException("Array is not allocated");
			
			if (data.length != 1)
				throw new NumberFormatException("Array size is not agreed with boolean type");
			
			return data[0] != 0x00;
		}
		catch (NullPointerException ex) {
			throw new TCP.TCPException(ex.getMessage(), ex);	
		}	
		catch (NumberFormatException ex) {
			throw new TCPException(ex.getMessage(), ex);	
		}	
	}	
	
	protected byte[] string2ByteA(String data) 
	{
		return (data == null) ? null : data.getBytes();	
	}
	
	protected String byteA2String(byte[] data) 
	{
		return (data == null) ? null : new String(data);	
	}
	
	//private member variable
	private SocketType m_sockType;
	private ServerSocket m_serverSocket;
	private Socket m_socket;
	private DataOutputStream m_dos;
	private DataInputStream m_dis;
	private int m_portNo = -1;
	private String m_ipAddr;
//	private ByteBuffer m_buffer;
}
