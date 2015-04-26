package org.pocketcampus.plugin.news.server.tests;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.pocketcampus.platform.server.HttpClient;
import org.pocketcampus.plugin.news.server.NewsSource.Feed;
import org.pocketcampus.plugin.news.server.NewsSource.FeedItem;
import org.pocketcampus.plugin.news.server.NewsSourceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

/**
 * Tests for NewsSourceImpl.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public class NewsSourceTests {
	@Test
	public void feedNameIsRead() {
		Feed feed = getFeeds()[0];

		assertEquals("The first feed should be Mediacom, a.k.a. General.",
				"Général", feed.name);
	}
	
	@Test
	public void feedIdIsSet() {
		Feed feed = getFeeds()[0];

		assertEquals("The first feed should be Mediacom, a.k.a. General.",
				"mediacom", feed.id);
	}

	@Test
	public void oneFeedIsMain() {
		int mainCount = 0;
		for (Feed feed : getFeeds()) {
			if (feed.isMain) {
				mainCount++;
			}
		}

		assertEquals("There should be exactly one main feed.",
				1, mainCount);
	}

	@Test
	public void itemInfoIsRead() {
		FeedItem item = getItems()[0];

		assertEquals("The item's title should be set correctly.",
				"Sous l'effet de la vitesse, le ballast s'envole", item.title);

		assertEquals("The item's link should be set correctly.",
				"http://actu.epfl.ch/news/sous-l-effet-de-la-vitesse-le-ballast-s-envole-2", item.link);

		assertEquals("The item's publication date should be set correctly.",
				new DateTime(2014, 3, 5, 5, 41, 46, DateTimeZone.UTC), item.publishDate.toDateTime(DateTimeZone.UTC));

		assertEquals("The item's image should be set, including size tokens.",
				"http://actu.epfl.ch/image/21037/{x}x{y}.jpg", item.imageUrl);
	}

	@Test
	public void itemContentIsRead() {
		FeedItem item = getItems()[0];

		assertEquals(
				"The item's content should be set correctly.",

				"<img height=\"181\" width=\"324\" src=\"http://actu.epfl.ch/image/21037/324x182.jpg\">"
						+ "\n"
						+ "\n<div>"
						+ "\n05.03.14 - Les résultats d’une étude de l’EPFL sur les projections de ballast lors de conditions hivernales exceptionnelles ont permis aux CFF de prendre des mesures permettant d’améliorer la sécurité aux abords des voies. <br />"
						+ "\n<p>Les CFF transportent chaque jour près d’un million de personnes, par tous les temps, y compris lors de conditions hivernales particulièrement difficiles. Dans le but d’améliorer la sécurité, les CFF ont donné mandat en 2012 au Centre de Transport de l’EPFL d’étudier le phénomène des projections de ballast par grand froid. Les résultats de l’étude ont permis de prendre des mesures d’amélioration.</p> <p>Pourquoi les cailloux volent-ils? L’analyse de ce phénomène rare, qui préoccupe aussi à l’étranger, confirme la présence conjointe de deux éléments: la vitesse élevée des trains et des conditions hivernales particulières. La conjonction de la neige, du vent et de températures très basses peut favoriser la formation de blocs de glace sous les voitures voyageurs. Ceux-ci peuvent se détacher et tomber brutalement sur la voie. Le choc du bloc de glace sur une particule de ballast peut alors la mettre en mouvement. Les cailloux sont alors projetés en l’air, ricochent sous le wagon, ou sont projetés plus loin par la roue.</p> <p>L’étude a permis aux chercheurs de déterminer quels endroits étaient les plus affectés: typiquement, l’Ouest et le Centre de la Suisse, avec en particulier les axes Genève–Lausanne, Lausanne–Brigue et Olten–Lucerne, recensaient le plus de cas. A chaque fois, les chercheurs ont noté une vitesse maximale autorisée du train de 140 ou 160 km/h, un enneigement important en hiver et, souvent, la proximité d’un lac ou d’un cours d’eau. Une explication possible: les trains chargés de neige descendant de l’axe Berne–Fribourg se réchauffent à Lausanne et peuvent provoquer des projections de ballast sur la Côte vaudoise. A noter aussi que de nombreux cas d’envol de ballast ont eu lieu à l’intérieur des tunnels, notamment sur la ligne Olten–Bâle. Là aussi, c’est le réchauffement des amas de neige et de glace lors du passage en tunnel qui serait en cause.</p> <p><strong>Des propositions mises en œuvre </strong><br /> Les chercheurs ont tenté d’identifier des causes potentielles, liées au type de matériel roulant ou encore à leur isolation. Enfin, au niveau des voies, le passage des roues du train sur les «joints collés» (entre deux tronçons de rails) ainsi que sur les aiguillages pourrait faire tomber la glace et ainsi déclencher le phénomène d’envol de ballast.</p> <p>Diverses pistes d’action ont été tracées par les chercheurs. L’amélioration du recensement des cas permet de mieux comprendre l’évolution du phénomène. Les CFF ont du reste déjà mis en place une déclaration systématique. Des limitations de vitesse en cas de fort enneigement font également partie des mesures prises.<br /> <br /> Les chercheurs de l’EPFL préconisent aussi de se concentrer sur l’élimination des points faibles: remplacer les joints collés par de longs rails soudés, en commençant par les zones les plus peuplées; utiliser des trains récents (de type ICN) sur les tronçons sensibles et poursuivre la pose de murs anti-bruit, qui pourraient aussi avoir pour fonction de capter des envols de ballast éventuels.</p> <p>«Toutes les propositions des chercheurs de l’EPFL, ainsi que d’autres, ont été prises en considération et évaluées en termes de faisabilité, d’efficacité et de coût par les CFF. Les meilleures mesures ont été sélectionnées pour une mise en œuvre», précisent les CFF. En décembre 2012, des réductions de vitesse locales d’une durée de quelques heures avaient été décidées. Elles avaient créé des retards pour la clientèle, mais permis d’assurer une sécurité maximale. Des contrôles supplémentaires du matériel roulant en hiver, un abaissement du niveau de ballast pour réduire les effets des chutes de blocs de glace et un traitement du dessous des wagons au moyen d’une laque qui empêche la glace d’adhérer ont également permis d’améliorer la sécurité ferroviaire. L’hiver 2013-2014, plutôt clément, n’a pour le moment pas nécessité de mesures d’abaissement de vitesse sur le réseau.</p>"
						+ "\n</div>",
				item.content);
	}

	private static Feed[] getFeeds() {
		// all of them are the same, though...
		return new NewsSourceImpl(new TestHttpClient()).getFeeds("fr");
	}

	private static FeedItem[] getItems() {
		Feed feed = getFeeds()[0];
		return feed.items.values().toArray(new FeedItem[feed.items.size()]);
	}

	private static final class TestHttpClient implements HttpClient {
		private static final String RETURN_VALUE = getFileContents("ExampleRssFeed.xml");

		@Override
		public String get(String url, Charset charset) throws IOException {
			return RETURN_VALUE;
		}
		
		@Override
		public String post(String url, byte[] body, Charset charset) throws IOException {
			throw new RuntimeException("post(String, byte[], Charset) should not be called.");
		}

		@SuppressWarnings("resource")
		private static String getFileContents(String name) {
			Scanner s = null;

			try {
				InputStream stream = new TestHttpClient().getClass().getResourceAsStream(name);
				s = new Scanner(stream, "UTF-8").useDelimiter("\\A");
				return s.hasNext() ? s.next() : "";
			} finally {
				if (s != null) {
					s.close();
				}
			}
		}
	}
}