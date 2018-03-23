import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 */

/**
 * @author BlackStone
 *
 */
public class ProviderStart {
	/** 
	 * @throws IOException 
	 * @ClassName: DubboStart 
	 * @Description: TODO(这里用一句话描述这个类的作用) 
	 * @date 2017年8月29日 下午3:34:05 
	 * 
	 */
	public static void main(String[] args) throws IOException{
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		System.out.println("服务启动");
		System.in.read();
		
		//com.alibaba.dubbo.container.Main.main(new String[]{"spring","log4j"});
		
		
	}
}
