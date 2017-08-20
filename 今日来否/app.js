//app.js
App({
  onLaunch: function() {
    wx.getUserInfo({
      withCredentials: false,
      success: res => {
        console.log(res)
        this. userInfo=res.userInfo
        if(this.userInfoCallback){
          this.userInfoCallback()
        }
      }
    })
  },
  userInfo:null,
  userInfoCallback:null
})
