<template>
  <div>
    <!-- Hero Section -->
    <div class="container-fluid d-flex justify-content-center align-items-center"
      style="height: 60vh; padding-top: 20px;">
      <div class="text-center">
        <h1 style="font-size: 3rem; font-weight: 700; color: #1A76D1;">연세셔틀</h1>
        <p style="font-size: 1.2rem; color: #555; margin-top: 15px;">
          연세대학교 미래캠퍼스 셔틀버스 운행 정보 서비스
        </p>
      </div>
    </div>

    <!-- Feature Cards Section -->
    <section class="schedule" style="margin-top: -50px; margin-bottom: 100px;">
      <div class="container">
        <div class="row">
          <!-- 카드 1: 실시간 위치 -->
          <div class="col-lg-4 col-md-6 col-12 mb-4">
            <router-link to="/live-map" style="text-decoration: none;">
              <div class="single-schedule">
                <div class="inner">
                  <div class="icon">
                    <i class="fa fa-clock"
                      style="position: absolute; font-size: 110px; color: #fff;
                        opacity: 0.2; right: -10px; bottom: -30px;"></i>
                  </div>
                  <div class="single-content">
                    <h4>셔틀 실시간 위치 확인</h4>
                    <p>연세시내셔틀 위치와 도착 예정 시간을 실시간으로 제공하여
                      불필요한 기다림과 셔틀을 놓치는 경우를 방지합니다.</p>
                  </div>
                </div>
              </div>
            </router-link>
          </div>

          <!-- 카드 2: 시간표 -->
          <div class="col-lg-4 col-md-6 col-12 mb-4">
            <router-link to="/routes" style="text-decoration: none;">
              <div class="single-schedule">
                <div class="inner">
                  <div class="icon">
                    <i class="fa fa-calendar-days"
                      style="position: absolute; font-size: 110px; color: #fff;
                        opacity: 0.2; right: -10px; bottom: -45px;"></i>
                  </div>
                  <div class="single-content">
                    <h4>노선별 시간표 확인</h4>
                    <p>연세셔틀 노선별 시간표, 노선의 각 정류장별 도착 예정 시간을
                      한 눈에 확인할 수 있습니다.</p>
                  </div>
                </div>
              </div>
            </router-link>
          </div>

          <!-- 카드 3: 예약 -->
          <div class="col-lg-4 col-md-12 col-12 mb-4">
            <router-link to="/reservation" style="text-decoration: none;">
              <div class="single-schedule">
                <div class="inner">
                  <div class="icon">
                    <i class="fa fa-check-square"
                      style="position: absolute; font-size: 110px; color: #fff;
                        opacity: 0.2; right: -10px; bottom: -50px;"></i>
                  </div>
                  <div class="single-content">
                    <h4>예약 시스템</h4>
                    <p>해당 날짜에 예약 가능한 셔틀을 한 눈에 보고 예약,
                      예약 내역 확인, 예약 취소가 가능합니다.</p>
                  </div>
                </div>
              </div>
            </router-link>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>