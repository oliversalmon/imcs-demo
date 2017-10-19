package com.example.mu.kafka.schemas;

import com.example.mu.domain.Trade;
import com.google.gson.Gson;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.util.serialization.DeserializationSchema;

import java.io.IOException;

public class TradeSchema implements DeserializationSchema<Trade> {

    private final Gson gson = new Gson();

    /**
     * Deserializes the byte message.
     *
     * @param message The message, as a byte array.
     * @return The deserialized message as an object (null if the message cannot be deserialized).
     */
    @Override
    public Trade deserialize(byte[] message) throws IOException {
        return gson.fromJson(new String(message), Trade.class);
    }

    /**
     * Method to decide whether the element signals the end of the stream. If
     * true is returned the element won't be emitted.
     *
     * @param nextElement The element to test for the end-of-stream signal.
     * @return True, if the element signals end of stream, false otherwise.
     */
    @Override
    public boolean isEndOfStream(Trade nextElement) {
        return false;
    }

    /**
     * Gets the data type (as a {@link TypeInformation}) produced by this function or input format.
     *
     * @return The data type produced by this function or input format.
     */
    @Override
    public TypeInformation<Trade> getProducedType() {
        return TypeInformation.of(Trade.class);
    }
}
