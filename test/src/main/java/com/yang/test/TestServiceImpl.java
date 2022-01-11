package com.yang.test;


import org.springframework.stereotype.Service;


@Service
public class TestServiceImpl implements TestService{
    @Override
    public void test() {
        System.out.println("123");
    }
}
