
// if logger doesn't exist, create stub
if (!window.console) console = {};
console.log = console.log || function(){};

function popupDefinition( word )
{
	$.colorbox({iframe:true, width:"80%", height:"80%", href:"/Definition.html?{word:'" + word + "'}"});
}

function formatScoredWord( scoredWord )
{
	return scoredWord.word.toUpperCase() + ' (' + scoredWord.score + ')';
}

function processResults( scoredWords )
{
	console.log( 'Got ' + scoredWords.length + ' results' );
	
	if ( scoredWords.length == 0 )
	{
		setResults('No words found');
		return;
	}
	
	// group the words by length
	var wordsGroupedByLength = [];
	for ( i in scoredWords )
	{
		var groupIndex = scoredWords[i].word.length - 1;
		var wordIndex;
		if ( wordsGroupedByLength[groupIndex] == null )
		{
			wordIndex = 0;
			wordsGroupedByLength[groupIndex] = [];
		}
		else
		{
			wordIndex = wordsGroupedByLength[groupIndex].length;
		}
		wordsGroupedByLength[groupIndex][wordIndex] = scoredWords[i];
	}
	
	// format the words into HTML sections
	var finalResults = 'Found ' + scoredWords.length + ' words:<br/><table>';
	var numberOfColumns = 6;
	for ( groupIndex = wordsGroupedByLength.length - 1; groupIndex >= 0; groupIndex-- )
	{
		if ( wordsGroupedByLength[groupIndex] != null &&
			 wordsGroupedByLength[groupIndex].length > 0 )
		{
			finalResults += '<tr><td colspan="' + numberOfColumns + '">' + ( groupIndex + 1 ) + ' letter words</td></tr><tr>';
			for ( wordIndex in wordsGroupedByLength[groupIndex] )
			{
				finalResults += '<td><a class="iframe" href="javascript:popupDefinition(\'' + wordsGroupedByLength[groupIndex][wordIndex].word + '\');">' + formatScoredWord( wordsGroupedByLength[groupIndex][wordIndex] ) + '</a></td>';
				
				if ( ( parseInt( wordIndex ) + 1 ) % numberOfColumns == 0 )
				{
					if ( wordIndex < wordsGroupedByLength[groupIndex].length - 1 )
					{
						finalResults += '</tr><tr>';
					}
				}
			}
			if ( ( wordsGroupedByLength[groupIndex].length % numberOfColumns ) != 0 )
			{
				for ( i = ( wordsGroupedByLength[groupIndex].length % numberOfColumns ); i < numberOfColumns; i++ )
				{
					finalResults += '<td></td>';
				}
			}
			finalResults += '</tr>';
		}
	}
	finalResults += '</table>';
	
	setResults( finalResults );
}

function searchServer( characters, beginsWith, contains, endsWith )
{
    console.log( characters + ', ' + beginsWith + ', ' + contains + ', ' + endsWith );
	$.getJSON( "/ScrabbleRest.html?{characters:'" + characters + "',beginsWith:'" + beginsWith + "',contains:'" + contains + "',endsWith:'" + endsWith+ "'}",
			   processResults );
}

var currentSearchNumber = 0;
function scheduleSearch()
{
	if ( isSearchValid() )
	{
		currentSearchNumber++;
		setTimeout( "scheduleSearchCallback( '" + $('#characters').val() + "','" + $('#beginsWith').val() + "','" + $('#contains').val() + "','" + $('#endsWith').val() + "'," + currentSearchNumber + " );", 300 );
	}
	else
	{
		clearResults();
	}
}
function scheduleSearchCallback( characters, beginsWith, contains, endsWith, searchNumber )
{
	// do not perform search if the user is still typing, this is detected
	// by incrementing the currentSearchNumber after each key stroke. The user
	// must pause for at least 200 milliseconds before a search takes place
	// this improves performance an reduces the frequency of search responses
	// arriving out of order
	if ( searchNumber == currentSearchNumber )
	{
	   searchServer( characters, beginsWith, contains, endsWith );
	}
}
function searchNow()
{
	if ( isSearchValid() )
	{
		currentSearchNumber++;
		searchServer( $('#characters').val(), $('#beginsWith').val(), $('#contains').val(), $('#endsWith').val() );
	}
	else
	{
		clearResults();
	}
}
function isSearchValid()
{
	return ( $('#characters').val().length > 1 );
}
function clearResults()
{
	$('#results').html( '' );
}
function setResults( html )
{
	$('#results').html( html );
}