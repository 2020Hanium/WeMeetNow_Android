package hanium.android.wemeetnow.network;

import hanium.android.wemeetnow.model.JoinModel;
import hanium.android.wemeetnow.model.LoginModel;
import hanium.android.wemeetnow.model.SuccessResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitService {
    @POST("/user/login")
    Call<SuccessResponse> userLogin(@Body LoginModel model);

    @POST("/user/join")
    Call<SuccessResponse> userJoin(@Body JoinModel model);
}
