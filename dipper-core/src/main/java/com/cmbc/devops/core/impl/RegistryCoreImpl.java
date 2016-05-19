package com.cmbc.devops.core.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;

import org.apache.log4j.Logger;

import com.cmbc.devops.bean.Result;
import com.cmbc.devops.constant.DockerConstants;
import com.cmbc.devops.core.RegistryCore;
import com.cmbc.devops.util.CommandExcutor;
import com.cmbc.devops.util.SSH;

public class RegistryCoreImpl implements RegistryCore {
	
	private static final Logger logger = Logger.getLogger(RegistryCoreImpl.class);

	public Result createRegistry(String ip, String name, String password,String fileName, String imageName,String imageTag) {
		SSH ssh = CommandExcutor.getSsh(ip, name, password);
		try {
			if (ssh.connect()) {
				String tag = imageName+":"+imageTag;
				StringBuilder commandStr = new StringBuilder();
				commandStr.append("cd ").append(DockerConstants.DOCKERFILE_PATH).append(";");
				if(fileName.contains(".tar.gz")){
					commandStr.append("tar zxvf "+fileName+";");
					commandStr.append("docker build -t "+tag+" "+DockerConstants.DOCKERFILE_PATH+fileName.substring(0, fileName.length()-7)+"/;");
					commandStr.append("rm -rf "+DockerConstants.DOCKERFILE_PATH+fileName.substring(0, fileName.length()-7)+"*;");
				}else if (fileName.contains(".zip")) {
					commandStr.append("unzip "+fileName+";");
					commandStr.append("docker build -t "+tag+" "+DockerConstants.DOCKERFILE_PATH+fileName.substring(0, fileName.length()-4)+"/;");
					commandStr.append("rm -rf "+DockerConstants.DOCKERFILE_PATH+fileName.substring(0, fileName.length()-4)+"*;");
				}else{
					return new Result(false, "不支持此类文件类型！");
				}
				try {
					String result = ssh.executeWithResult(commandStr.toString());
					logger.info("Make image result:"+result);
					BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(result.getBytes())));
					String uuid = null;
					int stepNull = 0;
					while(stepNull<2){
						String line = reader.readLine();
						if(null == line){
							stepNull++;
						}else if(line.contains("Successfully")&&line.contains("built")){
							uuid = line.split(" ")[2];
						}else{
							continue;
						}
					}
					if(null == uuid){
						return new Result(false, "镜像制作脚本执行异常！");
					}else{
						return new Result(true, uuid);
					}
				} catch (Exception e) {
					logger.error("Make image error", e);
					return new Result(false, "制作镜像失败：脚本执行异常！");
				}finally{
					ssh.close();
				}
			}else{
				return new Result(false, "登录仓库主机异常");
			}
		} catch (SocketException e) {
			logger.error("ssh connect error:", e);
			return new Result(false, "获取仓库主机连接失败");
		} catch (IOException e) {
			logger.error("ssh connect error:", e);
			return new Result(false, "获取仓库主机连接失败");
		}
	}

}
