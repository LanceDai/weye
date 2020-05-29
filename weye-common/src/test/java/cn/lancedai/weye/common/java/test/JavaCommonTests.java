package cn.lancedai.weye.common.java.test;

import cn.lancedai.weye.common.model.record.ServerCollectorRecord;
import cn.lancedai.weye.common.serializer.kafka.RecordDeserializer;
import cn.lancedai.weye.common.serializer.kafka.RecordSerializer;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class JavaCommonTests {
    @Test
    public void SerializeTest() {
        val record = new ServerCollectorRecord();
        val bytes = new RecordSerializer().serialize("topic", record);
        val obj = new RecordDeserializer().deserialize("topic", bytes);
        assert obj instanceof ServerCollectorRecord;
    }
}
