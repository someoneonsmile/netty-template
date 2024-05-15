package com.example.nettydemo.constants;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class EnvConst {

    @Value("${adapter.command.port}")
    private Integer adapterCommandPort;

}
