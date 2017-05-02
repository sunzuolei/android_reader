/**
 *
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.myreader.ui.presenter;


import com.myreader.api.BookApi;
import com.myreader.base.RxPresenter;
import com.myreader.entity.BookListTags;
import com.myreader.ui.contract.SubjectBookListContract;
import com.myreader.util.LogUtils;
import com.myreader.util.RxUtil;
import com.myreader.util.StringUtils;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * @author yuyh.
 * @date 2016/8/31.
 */
public class SubjectBookListPresenter extends RxPresenter<SubjectBookListContract.View> implements SubjectBookListContract.Presenter<SubjectBookListContract.View> {

    private BookApi bookApi;

    @Inject
    public SubjectBookListPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void getBookListTags() {
        String key = StringUtils.creatAcacheKey("book-list-tags");
        Observable<BookListTags> fromNetWork = bookApi.getBookListTags()
                .compose(RxUtil.<BookListTags>rxCacheListHelper(key));

        //依次检查disk、network
        Subscription rxSubscription = Observable.concat(RxUtil.rxCreateDiskObservable(key, BookListTags.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookListTags>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("getBookListTags:" + e.toString());
                        mView.showError();
                    }

                    @Override
                    public void onNext(BookListTags tags) {
                        mView.showBookListTags(tags);
                    }
                });
        addSubscrebe(rxSubscription);
    }

}
