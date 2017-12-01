package com.tencent.appframework.adapter.component;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.tencent.appframework.adapter.base.BaseViewTypeAdapter;


/**
 * Created by rubinqiu on 2017/3/8.
 * 首页测试组件的名称和类型
 */
public class ComponentName implements BaseViewTypeAdapter.ViewTypeData, Parcelable,Comparable<ComponentName> {
    private String name;
    private Class<?> activityClass;

    public ComponentName(Class<?> activityClass){
        this.name = activityClass.getSimpleName();
        this.activityClass = activityClass;
    }

    public String getName() {
        return name;
    }

    public Class<?> getActivityClass() {
        return activityClass;
    }

    @Override
    public int getViewType() {
        return 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeSerializable(this.activityClass);
    }

    protected ComponentName(Parcel in) {
        this.name = in.readString();
        this.activityClass = (Class<?>) in.readSerializable();
    }

    public static final Parcelable.Creator<ComponentName> CREATOR = new Parcelable.Creator<ComponentName>() {
        @Override
        public ComponentName createFromParcel(Parcel source) {
            return new ComponentName(source);
        }

        @Override
        public ComponentName[] newArray(int size) {
            return new ComponentName[size];
        }
    };

    @Override
    public int compareTo(@NonNull ComponentName o) {
        return name.compareTo(o.getName());
    }
}
