package com.mercu.config;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
//@EnableWebSecurity
//@ComponentScan(basePackages = {"com.mercu"})
public class WebSecurityConfig { // extends WebSecurityConfigurerAdapter {
//    @Autowired
//    private MemberUserDetailsService memberUserDetailsService;
//
//    @Override
//    public void configure(WebSecurity webSecurity) throws Exception {
//        webSecurity.ignoring().antMatchers("/static/**");
//    }
//
//    @Override
//    public void configure(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.authorizeRequests()
////                .antMatchers("/bl").hasRole("ADMIN")
////                .antMatchers("/partCategory/**").hasRole("ADMIN")
//                .antMatchers("/**").permitAll()
//                .and().httpBasic().realmName("mercu")
//                .authenticationEntryPoint(memberBasicAuthenticationEntryPoint());
//    }
//
//    @Bean
//    public BasicAuthenticationEntryPoint memberBasicAuthenticationEntryPoint() {
//        return new MemberBasicAuthenticationEntryPoint();
//    }
//
//    public class MemberBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
//        @Override
//        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//            response.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().println("HTTP Status 401 - " + authException.getMessage());
//        }
//
//        @Override
//        public void afterPropertiesSet() throws Exception {
//            setRealmName("mercu");
//            super.afterPropertiesSet();
//        }
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder)  throws Exception {
//        authenticationManagerBuilder.userDetailsService(memberUserDetailsService).passwordEncoder(passwordEncoder());
//    }
}
