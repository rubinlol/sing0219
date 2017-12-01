package com.tencent.pattern.builder;

/**
 * Created by rubinqiu on 2017/1/5.
 * 主要用于构造方法比较多参数的类，特别是可选的参数比较多的时候比较适用
 * 优点比较灵活
 */
public class Message {
    private int id;
    private String name;
    private Object tagert;
    private Object source;

    private Message(Builder builder){
        id = builder.id;
        name = builder.name;
        tagert = builder.tagert;
        source = builder.source;
    }

    public static class Builder implements IBuilder<Message>{
        private int id;
        private String name;
        private Object tagert;
        private Object source;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setTagert(Object tagert) {
            this.tagert = tagert;
            return this;
        }

        public Builder setSource(Object source) {
            this.source = source;
            return this;
        }

        @Override
        public Message builder() {
            return new Message(this);
        }
    }
}
