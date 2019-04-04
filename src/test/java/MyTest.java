import com.elec.Broadcast;
import java.io.IOException;

/**
 * @Name: MyTest
 * @Description: 功能描述
 * @Copyright: Copyright (c) 2018
 * @Author: xiongzhenyu
 * @Create Date : 2019/3/26 19:36
 * @Version: 1.0.0
 */
public class MyTest {

    public static void main(String[] args) throws InterruptedException {
        Broadcast broadcast= new Broadcast(9999);

        try {
            for (int i = 0; i < 100; i++) {
                broadcast.send("send data -- "+i,9999);
                System.out.println("第--"+i+"--次发送数据");
                Thread.sleep(2000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
