@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LinkServiceImpl linkService;

    @Test
    void shouldCreateLinkSuccessfully() {

        User user = new User();
        user.setUsername("test");

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(user));

        when(linkRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        ShortLink link = linkService.create("https://google.com", "test");

        assertNotNull(link.getShortCode());
        assertEquals("https://google.com", link.getOriginalUrl());
    }
}
