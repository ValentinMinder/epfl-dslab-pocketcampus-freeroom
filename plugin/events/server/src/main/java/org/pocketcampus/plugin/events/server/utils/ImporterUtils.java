package org.pocketcampus.plugin.events.server.utils;

public class ImporterUtils {

	public static int getCategFromName(String categName) {
		if ("Conferences - Seminars".equals(categName) || "Conférences - Séminaires".equals(categName))
			return 1;
		if ("Meetings management tips".equals(categName) || "Assemblées Conseils Direction".equals(categName))
			return 2;
		if ("Miscellaneous".equals(categName) || "Divers".equals(categName))
			return 4;
		if ("Exhibitions".equals(categName) || "Expositions".equals(categName))
			return 5;
		if ("Movies".equals(categName) || "Films".equals(categName))
			return 6;
		if ("Celebrations".equals(categName) || "Fêtes".equals(categName))
			return 7;
		if ("Inaugural lessons - Lessons of honor".equals(categName) || "Leçons inaugurales - Leçons d'honneur".equals(categName))
			return 8;
		if ("Cultural events".equals(categName) || "Manifestations culturelles".equals(categName))
			return 9;
		if ("Sporting events".equals(categName) || "Manifestations sportives".equals(categName))
			return 10;
		if ("Dating EPFL - economy".equals(categName) || "Rencontres EPFL – économie".equals(categName))
			return 11;
		if ("Thesis defenses".equals(categName) || "Soutenances de thèses".equals(categName))
			return 12;
		if ("Academic calendar".equals(categName) || "Calendrier Académique".equals(categName))
			return 13;
		return Integer.MAX_VALUE;
	}
}
