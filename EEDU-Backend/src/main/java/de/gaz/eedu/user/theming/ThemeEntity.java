package de.gaz.eedu.user.theming;

import de.gaz.eedu.user.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity public class ThemeEntity
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private UserEntity userEntity;
    private String name;
    private int[] green = {
            0x077f1b,
            399949,
            0x6ab276,
            0x9ccca4,
            0xcde5d1
    },
    blue = {
            0x222d65,
            0x4e5784,
            0x7a81a3,
            0xa7abc1,
            0xd3d5e0
    },
    red = {
            0xa53c26,
            0xb76351,
            0xc98a7d,
            0xdbb1a8,
            0xedd8d4
    };

}
