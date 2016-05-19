package com.cmbc.devops.core;

import com.cmbc.devops.bean.PathObject;
import com.cmbc.devops.bean.Result;

/**
 * 镜像制作核心接口，用户登录到仓库主机进行镜像的相关操作，主要包括镜像的制作，打标和发布
 * 
 * @author dmw
 * @version 1.0
 * @since 1.0
 */
public interface ImageCore {
	/**
	 * 制作镜像，SSH登录仓库主机并执行镜像制作脚本
	 * 
	 * @param ip
	 *            仓库主机IP
	 * @param name
	 *            仓库主机用户名
	 * @param password
	 *            登录密码
	 * @param fileName
	 *            需要解压的镜像文件名
	 * @param imageName
	 *            镜像名称
	 * @param tag
	 *            镜像版本
	 * @return 镜像是否制作成功，成功后返回的message值代表镜像的UUID且success = true；否则succes =
	 *         false，且message值为失败的原因
	 * @throws Exception
	 */
	public abstract Result makeImage(String ip, String name, String password, String fileName, String imageName,
			String tag, boolean basic, String imageUuid);

	/**
	 * 镜像打标 ：SSH登录仓库主机并执行镜像打标脚本
	 * 
	 * @param ip
	 *            主机IP
	 * @param name
	 *            用户名
	 * @param password
	 *            登录密码
	 * @param source
	 *            源镜像
	 * @param target
	 *            目标镜像
	 * @return 镜像是否打标成功，成功后返回success = true；否则succes = false，且message值为失败的原因
	 */
	public abstract Result tagImage(String masterIp, String name, String password, String source, String target);

	public abstract Result tagLoadImage(String masterIp, String name, String password, String imageUuid, String target);

	/**
	 * 镜像发布：SSH登录仓库主机，并执行镜像发布脚本
	 * 
	 * @param ip
	 *            主机IP
	 * @param name
	 *            用户名
	 * @param password
	 *            登录密码
	 * @param image
	 *            镜像全程
	 * @return 镜像发布是否成功，成功后返回success = true；否则succes = false，且message值为失败的原因
	 */
	public abstract Result pushImage(String masterIp, String name, String password, String image);

	/**
	 * 查询某个路径下的文件列表内容
	 * 
	 * @param masterIp
	 *            主机IP地址
	 * @param name
	 *            登陆用户名
	 * @param password
	 *            用户密码
	 * @param path
	 *            查看的路径
	 * @return 返回路径下的所有包含的文件列表
	 * @throws Exception
	 * 
	 **/
	public abstract PathObject queryPathElement(String masterIp, String name, String password, String path);

	/**
	 * @param ip
	 * @param name
	 * @param password
	 * @param fileFolder
	 * @param fileName
	 * @param imageName
	 * @param imageTag
	 * @param basic
	 * @return
	 * @throws Exception
	 *             后台远程制作镜像
	 */
	public Result makeRemoteImage(String ip, String name, String password, String fileFolder, String fileName,
			String imageName, String imageTag, boolean basic);

	/**
	 * @param hostIp
	 * @param hostUser
	 * @param hostPwd
	 * @param folder
	 * @return
	 * @throws Exception
	 *             在远程仓库服务器上创建临时文件夹
	 */
	public Result createRegTempFolder(String hostIp, String hostUser, String hostPwd, String folder);

	/**
	 * @param hostIp
	 * @param hostUser
	 * @param hostPwd
	 * @param folder
	 * @return
	 * @throws Exception
	 *             删除仓库服务器上的文件夹，用于编译完成或者失败的情况下，做收尾处理
	 */
	public Result deleteRegTempFolder(String hostIp, String hostUser, String hostPwd, String folder);

	/**
	 * @param hostIp
	 * @param hostUser
	 * @param hostPwd
	 * @param imageInfo
	 * @param imageName
	 * @param imageTag
	 * @return
	 */
	public Result exportImage(String hostIp, String hostUser, String hostPwd, String imageInfo, String imageName,
			String imageTag, String savePath);

}
