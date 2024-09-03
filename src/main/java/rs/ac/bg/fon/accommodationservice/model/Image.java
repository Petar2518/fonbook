package rs.ac.bg.fon.accommodationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Data
@Entity
@Table(name = "images")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "images_id_seq")
    @SequenceGenerator(name = "images_id_seq", sequenceName = "images_id_seq", allocationSize = 1)
    private Long id;

    @Lob
    @JdbcTypeCode(Types.VARBINARY)
    private byte[] image;


    @ManyToOne(fetch = FetchType.LAZY)
    private Accommodation accommodation;


}
