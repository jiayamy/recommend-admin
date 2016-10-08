/* ==========================================================
 * bootstrap-tag.js v2.2.5
 * https://github.com/fdeschenes/bootstrap-tag
 * ==========================================================
 * Copyright 2012 Francois Deschenes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================== */

!function ( $ ) {

  'use strict' // jshint ;_;

  var MyTag = function ( element, options ) {
    this.element = $(element)
    this.options = $.extend(true, {}, $.fn.mytag.defaults, options)
    this.values = $.grep($.map(this.element.val().split(','), $.trim), function ( value ) { return value.length > 0 })
    this.show()
  }

  MyTag.prototype = {
    constructor: MyTag

  , show: function () {
      var that = this

      that.element.parent().prepend(that.element.detach().hide())
      that.element
        .wrap($('<div class="tags">'))
        .parent()
        .on('click', function () {
          that.input.focus()
        })
        
        that.source = function(query, process) {
    	  var displayType = $('#'+that.options.displayTypeSelector).val()||'';
			  $.ajax({url: that.options.url+'?q='+encodeURIComponent(query)+'&displayType='+displayType})
			  .done(function(result_items){
				  var obj = result_items;
					process(obj.data);
			  });
			}
        
      that.input = $('<input type="text">')
        .attr('placeholder', that.options.placeholder)
        .insertAfter(that.element)
        .on('focus', function () {
          that.element.parent().addClass('tags-hover')
        })
        .on('blur', function () {
          if (!that.skip) {
            that.process()
            that.element.parent().removeClass('tags-hover')
            that.element.siblings('.tag').removeClass('tag-important')
          }
          that.skip = false
        })
        .on('keydown', function ( event ) {
          if ( event.keyCode == 188 || event.keyCode == 13 || event.keyCode == 9 ) {
            if ( $.trim($(this).val()) && ( !that.element.siblings('.typeahead').length || that.element.siblings('.typeahead').is(':hidden') ) ) {
              if ( event.keyCode != 9 ) event.preventDefault()
              that.process()
            } else if ( event.keyCode == 188 ) {
              if ( !that.element.siblings('.typeahead').length || that.element.siblings('.typeahead').is(':hidden') ) {
                event.preventDefault()
              } else {
                that.input.data('typeahead').select()
                event.stopPropagation()
                event.preventDefault()
              }
            }
          } else if ( !$.trim($(this).val()) && event.keyCode == 8 ) {
            var count = that.element.siblings('.tag').length
            if (count) {
              var tag = that.element.siblings('.tag:eq(' + (count - 1) + ')')
              if (tag.hasClass('tag-important')) that.remove(count - 1)
              else tag.addClass('tag-important')
            }
          } else {
            that.element.siblings('.tag').removeClass('tag-important')
          }
        })
        .bs_mytypeahead({
          source: that.source
          , valueField : this.options.valueField
          , displayField : this.options.displayField
          , promptField : this.options.promptField
        , updater: $.proxy(that.add, that)
        , items: 100
        })

      $(that.input.data('bs_mytypeahead').$menu).on('mousedown', function() {
        that.skip = true
      })
      
      if (that.values.length) {
    	  $.ajax({url: that.options.url+'?v='+encodeURIComponent(that.values)})
		  .done(function(result_items){
			  var obj = result_items;
			  
			  $.each(obj.data, function () {
				  var display = this[that.options.displayField];
			      if(this[that.options.promptField]) {
			    	  display = display + '<span class="orange">('+ this[that.options.promptField] +')</span>';
			      }
		         that.createBadge(display);
		         if(!that.options.multiSelect) {
		        	 if(that.values.length > 0) {
		        		 that.input.hide();
		        	 }
		         }
		      })
		  });
      }
      
      this.element.trigger('shown')
    }
  , inValues: function ( value ) {
      if (this.options.caseInsensitive) {
        var index = -1
        $.each(this.values, function (indexInArray, valueOfElement) {
          if ( valueOfElement.toLowerCase() == value.toLowerCase() ) {
            index = indexInArray
            return false
          }
        })
        return index
      } else {
        return $.inArray(value, this.values)
      }
    }
  , createBadge: function ( value ) {
    var that = this
    
    if(!value) {
    	return;
    }
    
      $('<span/>', {
        'class' : "tag"
      })
      .html(value)
      /*.append($('<button type="button" class="close">&times;</button>')*/
      .append($('<a href="javascript:void(0);" class="close">x</a>')
        .on('click', function () {
          that.remove(that.element.siblings('.tag').index($(this).closest('.tag')))
        })
      )
      .insertBefore(that.element)
  }
  , add: function ( item ) {
      var that = this
      if(!item) {
    	  return;
      }
      if(!that.options.multiSelect && this.values.length > 0) {
    	  return;  
      }
      
      var display = item[that.options.displayField];
      if(item[that.options.promptField]) {
    	  display = display + '<span class="orange">('+ item[that.options.promptField] +')</span>';
      }
      var value = item[that.options.valueField]+'';
      
      if ( !that.options.allowDuplicates ) {
        var index = that.inValues(value)
        if ( index != -1 ) {
          var badge = that.element.siblings('.tag:eq(' + index + ')')
          badge.addClass('tag-warning')
          setTimeout(function () {
            $(badge).removeClass('tag-warning')
          }, 500)
          return
        }
      }

      this.values.push(value)
      this.createBadge(display)

      this.element.val(this.values.join(', '))
      this.element.trigger('added', [value])
      
      if(!that.options.multiSelect) {
    	  if(this.values.length > 0) {
    		  that.input.hide();
    	  }
      }
    }
  , remove: function ( index ) {
	  var that = this;
      if ( index >= 0 ) {
        var value = this.values.splice(index, 1)
        this.element.siblings('.tag:eq(' + index + ')').remove()
        this.element.val(this.values.join(', '))

        this.element.trigger('removed', [value])
      }
      if(!that.options.multiSelect) {
    	  if(!this.values || this.values.length == 0) {
    		  that.input.show();
    	  }
	  }
    }
  , process: function () {
      var values = $.grep($.map(this.input.val().split(','), $.trim), function ( value ) { return value.length > 0 }),
          that = this
//      $.each(values, function() {
//        that.add(this)
//      })
      this.input.val('')
    }
  , skip: false
  }

  var old = $.fn.mytag

  $.fn.mytag = function ( option ) {
    return this.each(function () {
      var that = $(this)
        , data = that.data('tag')
        , options = typeof option == 'object' && option
      if (!data) that.data('tag', (data = new MyTag(this, options)))
      if (typeof option == 'string') data[option]()
    })
  }

  $.fn.mytag.defaults = {
    allowDuplicates: false
  , caseInsensitive: true
  , placeholder: ''
  , source: []
  , multiSelect: false
  }

  $.fn.mytag.Constructor = MyTag

  $.fn.mytag.noConflict = function () {
    $.fn.mytag = old
    return this
  }

//  $(window).on('load', function () {
//    $('[data-provide="tag"]').each(function () {
//      var that = $(this)
//      if (that.data('tag')) return
//      that.mytag(that.data())
//    })
//  })
}(window.jQuery);
