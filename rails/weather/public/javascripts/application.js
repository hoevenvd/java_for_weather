var Plugin = {
  setRating: function(plugin_id, release_id, points) {
    new Ajax.Request('/plugins/' + plugin_id + '/ratings', {parameters: 'rating[release_id]=' + release_id + '&rating[points]=' + points});
  }
}