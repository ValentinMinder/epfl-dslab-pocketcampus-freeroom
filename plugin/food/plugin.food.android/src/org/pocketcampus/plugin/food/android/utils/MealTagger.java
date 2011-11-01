package org.pocketcampus.plugin.food.android.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.pocketcampus.plugin.food.shared.Meal;

import android.util.Log;

public class MealTagger {

	private HashMap<MealTag, Collection<Pattern>> tagPatterns;
	
	/**
	 * Constructor without default Patterns
	 * @param tagPatterns a HashMap. To every MealTag there is a list of REGULAR-EXPRESSION-patterns included.
	 */
	public MealTagger(HashMap<MealTag, Collection<Pattern>> tagPatterns) {
		this.tagPatterns = tagPatterns;
		valid();
	}
	
	/**
	 * Constructor which adds default patterns
	 */
	public MealTagger() {
		
		this.tagPatterns = new HashMap<MealTag, Collection<Pattern>>();
		
		Pattern meatPatterns = Pattern.compile(".*(boeuf|caille|kangourou|kebab|poulet|bouillis|veau|agneau|porc|cheval|cerf|chevreuil|chasse|coq .?coquelet.?|canard|lard .?lardons.?|dinde|volaille|pintade|autruche|jambon|saucisse|merguez|burger|nugget|cordon.?bleu|chipolatas|carne.?.?chili.?con|hachis.?.?parmentier|moussaka|osso.?buco).*", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
//		Pattern meatPatterns2 = Pattern.compile(".*(�minc�|roti|rago�t|gigot|escalope|steak|brochette).*",Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
		Pattern fishPatterns = Pattern.compile(".*(poisson|carrelet|lotte|dorade|chevalier|cabillaud|saumon|pangasius|lieu|bar|mulet|truite|st.?Pierre|colin|perche|rougaille|calamars).*", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
		Pattern vegetarianPatterns = Pattern.compile(".*v.?g.?tarienne.*", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
		Pattern pastaPatterns = Pattern.compile(".*(p�tes|pasta|nouilles|gnocchi|raviolis|tortellinis|tortellis|cannellonis|triangolis|spaghettis|penne|cornettes|tagliatelle).*",Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
		Pattern ricePatterns = Pattern.compile(".*(riz|risotto|cantonais|casimir).*",Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
		Pattern porcPatterns = Pattern.compile(".*(porc|jambon|lard|lard .?lardons.?|saucisse|cordon.?bleu).*", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
		Pattern chickenPatterns = Pattern.compile(".*(poulet|coq .?coquelet.?|dinde|volaille|nugget).*", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
		Pattern beefPatterns = Pattern.compile(".*(boeuf|burger).*", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
		Pattern horsePatterns = Pattern.compile(".*(cheval).*", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
		
		addPattern(MealTag.MEAT, meatPatterns);
//		addPattern(MealTag.MEAT, meatPatterns2);
		addPattern(MealTag.FISH, fishPatterns);
		addPattern(MealTag.VEGETARIAN, vegetarianPatterns);
		addPattern(MealTag.PASTA, pastaPatterns);
		addPattern(MealTag.RICE, ricePatterns);
		addPattern(MealTag.PORC, porcPatterns);
		addPattern(MealTag.CHICKEN, chickenPatterns);
		addPattern(MealTag.BEEF, beefPatterns);
		addPattern(MealTag.HORSE, horsePatterns);
		
		valid();
	}
	
	public void addPattern(MealTag tag, Pattern pattern) {
		if (tag == null || pattern == null) {
			throw new IllegalArgumentException("Null-Parameter");
		}
		Collection<Pattern> patternsOfThisTagSoFar = this.tagPatterns.get(tag);
		if (patternsOfThisTagSoFar != null) {
			patternsOfThisTagSoFar.add(pattern);
			tagPatterns.put(tag, patternsOfThisTagSoFar); //TODO! is this needed?
		} else {
			HashSet<Pattern> newCollection = new HashSet<Pattern>();
			newCollection.add(pattern);
			tagPatterns.put(tag, newCollection);
		}
	}
	
	public Collection<Pattern> getPatternsFor(MealTag tag) {
		if (tag != null) {
			return tagPatterns.get(tag);
		}
		throw new IllegalArgumentException("tag is null");
	}
	
	/**
	 * 
	 * @param tag
	 * @param allMeals
	 * @return a collection of meals (subset of allMeals), which belong to the given tag 
	 *         or null if there is no Meal which belongs to the given tag.
	 */
	public Collection<Meal> parseMealsFor(MealTag tag, Collection<Meal> allMeals) {
		if (tag == null || allMeals == null) {
			throw new IllegalArgumentException("Null-Params");
		}
		Collection<Meal> resultMeals = new HashSet<Meal>();
		Collection<Pattern> patterns = tagPatterns.get(tag);
		if (patterns == null || patterns.size() == 0) {
			return null;
		}
		
		for (Meal meal: allMeals) {
			if(mealBelongsTo(tag, meal)) {
				resultMeals.add(meal);
			}
		}
		
		if (resultMeals.size() == 0) {
			return null;
		}
		return resultMeals;
	}
	
	public boolean mealBelongsTo(MealTag tag, Meal meal) {
		if (tag == null || meal == null) {
			throw new IllegalArgumentException("Null-Params");
		}
		Collection<Pattern> patterns = tagPatterns.get(tag);
		
		if (patterns == null || patterns.size() == 0) {
			return false;
		}
		
		String mealDescription = meal.getMealDescription();
		String mDescr = mealDescription.replaceAll("[\\r\\n]", " ");
		mDescr = mDescr.toLowerCase();
		String mealName = meal.getName();
		mealName = mealName.toLowerCase();
		String match = mealName.concat(mDescr);
		
		Log.d("TAGGER",""+match);
		
		//check the name or the description against all patterns
		for (Pattern pattern : patterns) {
			if ((match).matches(pattern.pattern())) {
				Log.d("TAGGER", "Return true!");
				return true;
			}
		}
		Log.d("TAGGER","--------------------------------");
		return false;
		
	}
	
	/**
	 * Extracts the sum of all tags which matches at least to one element of the given collection of meals.
	 * Usually, this is used to get all tags which match one Meal.
	 * @param meals a collection of meals
	 * @return a set which contains every MealTag which matches at least one element of the given collection of meals
	 */
	public Collection<MealTag> extractTagsFrom(Collection<Meal> meals) {
		
		if (meals == null) {
			return new HashSet<MealTag>();
		}
		
		Collection<MealTag> resultingTags = new HashSet<MealTag>();
		Collection<Meal> inputMealCollection = new HashSet<Meal>(meals);
		inputMealCollection.addAll(meals);

		
		for (MealTag tag : MealTag.values()) {
			Collection<Meal> parsedMeals = parseMealsFor(tag, inputMealCollection);
			
			if (parsedMeals == null) {
				continue;
			}
			
			if (parsedMeals.size() >= 1) {
				resultingTags.add(tag);
			}
			
		}
		return resultingTags;
	}

	private void valid() {
		if (this.tagPatterns == null) {
			throw new IllegalArgumentException("tagPatterns is null.");
		}
		for (Entry<MealTag, Collection<Pattern>> entry : tagPatterns.entrySet()) {
			Collection<Pattern> patterns = entry.getValue();
			MealTag tag = entry.getKey();
			if ( tag == null || patterns == null) {
				throw new IllegalArgumentException("Tag or CollectionOfPatterns is null");
			}
			for(Pattern pattern : patterns) {
				if (pattern == null) {
					throw new IllegalArgumentException("There is a null-pattern in the patterns-collection of Tag "+tag);
				}
			}
			
		}
		
	}
	
	
}