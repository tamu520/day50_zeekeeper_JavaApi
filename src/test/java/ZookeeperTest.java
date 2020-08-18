import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ZookeeperTest {
    private RetryPolicy retryPolicy=null;
    private CuratorFramework client=null;

    @Before
    public void init(){
        //RetryPolicy 重试策略的接口  实现类:ExponentialBackoffRetry( 重试间隔时间,最大重试次数,最大重试时间 )
        retryPolicy = new ExponentialBackoffRetry(1000,3,5000);
        //创建客户端( zookeeper的地址和端口, 会话超时时间(连接没动作) ,连接超时时间, 重试的策略对象 )
        client = CuratorFrameworkFactory.newClient("localhost:2181",2000,1000,retryPolicy);
        //开启客户端
        client.start();
    }

    //增加节点
    @Test
    public void test1() throws Exception {

        //1. 创建一个空节点(a)（只能创建一层节点）
        //client.create().forPath("/a");

        //2. 创建一个有内容的b节点（只能创建一层节点）
        //client.create().forPath("/b","创建b节点带值".getBytes());

        //3.创建多层节点
        //递归创建节点withMode(CreateMode.PERSISTENT)
        //创建持久性节点creatingParentsIfNeeded()
        client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT).forPath("/c/c1");

        //4.创建带序号的节点
        /*client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath("/d");*/

        //5.创建临时节点
        //withMode(CreateMode.EPHEMERAL)  临时节点
        /*client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL).forPath("/e");*/

        //6.创建带序号的临时节点
        /*client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/g");*/

        //Thread.sleep(5000);

    }

    //删除节点
    @Test
    public void test2() throws Exception {
        //client.delete().forPath("/d0000000007");

        //删除节点并删除所有子节点
        //client.delete().deletingChildrenIfNeeded().forPath("/c");

        //强删除,即使客户端断连接了,指令到达,剩下的服务器会自己解决
        client.delete().guaranteed().deletingChildrenIfNeeded().forPath("/c");
    }

    //修改节点
    @Test
    public void test3() throws Exception {
        client.setData().forPath("/b","你是白狗".getBytes());
    }

    //查询节点
    @Test
    public void test4() throws Exception {
        byte[] bytes = client.getData().forPath("/b");
        System.out.println(new String(bytes));
    }

    @After
    public void destroy(){
        //关闭
        client.close();
    }

}
