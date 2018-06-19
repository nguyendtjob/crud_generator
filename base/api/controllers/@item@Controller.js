module.exports = {

  create: function (req, res) {
    @itemparams@

      req.file('file').upload(function whenDone(err,uploadedFiles){
        if (err) {
          sails.log.error(new Error("Create: Error when uploading image file"));
        }

        //In case there is no upload
        if (uploadedFiles.length === 0) {
        @item@.create({
            @itemparamsquery@
          }).exec(function (err) {
            if (err) {
              sails.log.error(new Error("500: Database Error (create)"));
              res.send(500, {error: 'Database Error'});
            }
            res.redirect('@item@/adminlist');
          });
        }else {
          //Convert the uploaded file into base64 string, then delete the uploaded file from the disk
          var fs = require('fs');
          var bitmap = fs.readFileSync(uploadedFiles[0].fd);
          @conversion@
          fs.unlink(uploadedFiles[0].fd, function(err) {
            if (err) {
              sails.log.error(new Error("Create: Error when deleting file in the server"));
              return;
            }

          @item@.create({
              @itemparamsquery@
            }).exec(function (err) {
              if (err) {
                sails.log.error(new Error("500: Database Error (create)"));
                res.send(500, {error: 'Database Error'});
              }
              res.redirect('@item@/adminlist');
            });
          });
        }

      });
  },


  list: function (req, res) {
    @item@.find().exec(function (err, items){
      if (err) {
        sails.log.error(new Error("500: Database Error (search)"));
        res.send(500, {error: 'Database Error'});
      }
      res.view('list', {
        items: items,
      });
    });
  },


  adminlist: function (req, res) {
    @item@.find().exec(function (err, items) {
      if (err) {
        sails.log.error(new Error("500: Database Error (adminlist)"));
        res.send(500, {error: 'Database Error'});
      }
      res.view('adminlist', {
        items: items,
      });
    });
  },

  add: function(req, res) {
    res.view('add');
  },

  details: function (req, res) {
    @item@.findOne({id: req.params.id}).exec(function (err, item) {
      if (err) {
        sails.log.error(new Error("500: Database Error (details)"));
        res.send(500, {error: 'Database Error'});
      }

      if(!item){
        res.redirect('404');
      }else{
        res.view('details', {item: item});
      }
    })
  },


  edit: function (req, res) {
    @item@.findOne({id: req.params.id}).exec(function (err, item) {
      if (err) {
        sails.log.error(new Error("500: Database Error (edit)"));
        res.send(500, {error: 'Database Error'});
      }
      res.view('edit', {item: item});
    })
  },


  update: function(req, res){
    @itemparams@

    req.file('file').upload(function whenDone(err,uploadedFiles){
      if (err) {
        sails.log.error(new Error("Create: Error when uploading file"));
      }

      //In case there is no upload
      if (uploadedFiles.length === 0) {
      @item@.update({id: req.params.id},{
          @itemparamsquery@
        }).exec(function (err) {
          if (err) {
            sails.log.error(new Error("500: Database Error (update)"));
            res.send(500, {error: 'Database Error'});
          }
          res.redirect('@item@/adminlist');
        });
      }else {
        //Convert the uploaded file into base64 string, then delete the uploaded file from the disk
        var fs = require('fs');
        var bitmap = fs.readFileSync(uploadedFiles[0].fd);
        @conversion@
        fs.unlink(uploadedFiles[0].fd, function(err) {
          if (err) {
            sails.log.error(new Error("Update: Error when deleting file in the server"));
            return;
          }

        @item@.update({id: req.params.id}, {
            @itemparamsquery@
          }).exec(function (err) {
            if (err) {
              sails.log.error(new Error("500: Database Error (update)"));
              res.send(500, {error: 'Database Error'});
            }
            res.redirect('@item@/adminlist');
          });
        });
      }

    });
  },


  delete: function (req, res) {
    @item@.destroy({id: req.params.id}).exec(function (err) {
      if (err) {
        sails.log.error(new Error("500: Database Error (delete)"));
        res.send(500, {error: 'Database Error'});
      }
      res.redirect('@item@/adminlist');
    });
  }
};
