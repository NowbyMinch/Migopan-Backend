
@Entity
@Table(name = "grupo_membro")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class GrupoMembro {

    @EmbeddedId
    private GrupoMembroId id = new GrupoMembroId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuario")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("grupo")
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;
    
    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean bloqueado = false;
}