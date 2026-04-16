<template>
    <div class="footer">
      <div class="container">
        <div class="row">
          <div class="col-lg-6">
            <div class="footer-contact">
              <h2>연세셔틀</h2>
              <p><i class="fa fa-map-marker-alt"></i> 강원특별자치도 원주시 연세대길 1</p>
              <p><i class="fa fa-phone-alt"></i> 대표번호: 010 - 3912 - 6595</p>
              <p><i class="fa fa-phone-alt"></i> 민원담당자 (통학버스 사무소장) : 010 - 3793 - 7882</p>
              <p><i class="fa fa-envelope"></i> hanamybus@naver.com</p>
            </div>
          </div>
          <div class="col-lg-3">
            <div class="footer-links">
              <h2>Quick Links</h2>
              <ul>
                <li><router-link to="/notifications"><i class="fa fa-caret-right"></i>공지사항</router-link></li>
                <li><router-link to="/routes"><i class="fa fa-caret-right"></i>노선</router-link></li>
                <li><router-link to="/stops"><i class="fa fa-caret-right"></i>승하차 장소</router-link></li>
                <li><router-link to="/live-map"><i class="fa fa-caret-right"></i>실시간 위치</router-link></li>
                <li><router-link to="/reservation"><i class="fa fa-caret-right"></i>예약</router-link></li>
              </ul>
            </div>
          </div>
        </div>
      </div>
  
      <div class="container copyright">
        <div class="row">
          <div class="col-md-6">
            <p>&copy; <router-link to="/">연세셔틀</router-link>, All Right Reserved.</p>
          </div>
        </div>
      </div>
    </div>
  </template>