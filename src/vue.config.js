// build/webpack.base.conf.js
// module.exports = {
//   /** 其他与本次改动无关的配置 */
//   resolve: {
//     /** other code */
//     extensions: ['.js', '.vue', '.json', '.ts'], // 添加 .ts 扩展名
//   },
//    module: {
//     rules: [
//       /** other code */
//       {
//         test: /\.tsx?$/,
//         use: [
//           { loader: 'babel-loader' },
//           {
//             loader: 'ts-loader',
//             exclude: /node_modules/,
//             options: {
//               transpileOnly: true,
//               appendTsSuffixTo: [
//                 '\\.vue$'
//               ],
//               happyPackMode: false
//             }
//           }
//         ]
//       }
//     ]
//   },
   
//   // 添加 chainWebpack 配置
//   chainWebpack: config =>
//     {
//       config.
//   module
//         .
//   rule('markdown'
//   )
//         .
//   test(/\.md$/
//   )
//         .
//   use('html-loader'
//   )
//         .
//   loader('html-loader'
//   )
//         .
//   end
//   ()
//         .
//   use('markdown-loader'
//   )
//         .
//   loader('markdown-loader'
//   )
//         .
//   end
//   ();
//     }

// }


const path = require('path');

module.exports = {
  // 其他与本次改动无关的配置
  resolve: {
    // other code
    extensions: ['.js', '.vue', '.json', '.ts'], // 添加 .ts 扩展名
  },
  module: {
    rules: [
      // other code
      {
        test: /\.tsx?$/,
        use: [
          { loader: 'babel-loader' },
          {
            loader: 'ts-loader',
            exclude: /node_modules/,
            options: {
              transpileOnly: true,
              appendTsSuffixTo: [
                '\\.vue$'
              ],
              happyPackMode: false
            }
          }
        ]
      }
      // 添加对 .md 文件的加载规则
      // 注意：这应该在 rules 数组中添加，而不是替换其他规则
    ]
  },
  // 添加 chainWebpack 配置
  chainWebpack: config => {
    config.module
      .rule('markdown')
      .test(/\.md$/)
      .use('html-loader')
      .loader('html-loader')
      .end()
      .use('markdown-loader')
      .loader('markdown-loader')
      .end();
  }
};
