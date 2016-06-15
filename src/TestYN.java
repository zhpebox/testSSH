/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestYN {

	public static void main(String[] args) {
		String hostname = "192.168.62.164";
		int port = 22;
		String username = "root";
		String password = "root123";
		
		// 指明连接主机的IP地址
		Connection conn = new Connection(hostname, port);
		Session ssh = null;
		try {
			// 连接到主机
			conn.connect();
			// 使用用户名和密码校验
			boolean isconn = conn.authenticateWithPassword(username, password);
			if (!isconn) {
				System.out.println("用户名称或者是密码不正确");
			} else {
				System.out.println("已经连接OK");


				// 配置执行命令，zabbix 编译 -> 安装 -> 配置
				ssh = conn.openSession();
				ssh.execCommand("cd /home;yum install gcc gcc-c++ kernel-devel;");
				
//				ssh.getStdin().write("y\n".getBytes());
				
				// 获取Terminal屏幕上的输出并打印出来
				InputStream is = new StreamGobbler(ssh.getStdout());
				BufferedReader brs = new BufferedReader(new InputStreamReader(is));
				while (true) {
					String line = brs.readLine();
					if (line == null) {
						break;
					}
					System.out.println(line);
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			// 连接的Session和Connection对象都需要关闭
			if (ssh != null) {
				ssh.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		
	}
}