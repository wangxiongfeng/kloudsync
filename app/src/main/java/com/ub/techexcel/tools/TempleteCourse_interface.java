package com.ub.techexcel.tools;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by wang on 2018/5/23.
 */

public interface TempleteCourse_interface {

    @GET("Course/List")
    Call<ResponseBody> getCourseByTemplete(@Header("UserToken") String userToken, @Query("listType") int listType,
                                           @Query("type") int type,
                                           @Query("templateType") int templateType,
                                           @Query("TeacherID") int TeacherID,
                                           @Query("sortBy") int sortBy,
                                           @Query("order") int order,
                                           @Query("pageIndex") int pageIndex,
                                           @Query("pageSize") int pageSize
    );

    @GET("Course/List")
    Call<ResponseBody> getCourseByTemplete2(@Header("UserToken") String userToken, @Query("listType") int listType,
                                            @Query("type") int type,
                                            @Query("templateType") int templateType,
                                            @Query("SchoolID") int SchoolID,
                                            @Query("sortBy") int sortBy,
                                            @Query("order") int order,
                                            @Query("pageIndex") int pageIndex,
                                            @Query("pageSize") int pageSize
    );


    @GET("Course/List")
    Observable<ResponseBody> getCourseByRxJava(@Header("UserToken") String userToken, @Query("listType") int listType,
                                               @Query("type") int type,
                                               @Query("templateType") int templateType,
                                               @Query("SchoolID") int SchoolID,
                                               @Query("TeacherID") int TeacherID,
                                               @Query("sortBy") int sortBy,
                                               @Query("order") int order,
                                               @Query("pageIndex") int pageIndex,
                                               @Query("pageSize") int pageSize
    );



}
